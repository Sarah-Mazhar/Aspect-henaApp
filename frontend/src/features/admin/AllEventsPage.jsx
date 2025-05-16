import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const API_BASE = "http://localhost:8080/api";

export default function AllEventsPage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const adminId = localStorage.getItem("adminId");

  useEffect(() => {
    if (!token || role !== "ADMIN" || !adminId) {
      alert("Unauthorized access. Redirecting to login...");
      navigate("/login");
      return;
    }

    const staticCreds = {
      ADMIN: { username: "admin", password: "adminpass" },
    };

    const authHeader = btoa(
      `${staticCreds.ADMIN.username}:${staticCreds.ADMIN.password}`
    );

    axios
      .get(`${API_BASE}/event/admin/${adminId}`, {
        headers: { Authorization: `Basic ${authHeader}` },
      })
      .then((res) => {
        setEvents(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error fetching events:", err);
        setLoading(false);
      });
  }, [adminId, role, token, navigate]);

  if (loading) return <p style={{ color: "#fff" }}>Loading events...</p>;

  return (
    <div style={{ padding: "2rem", color: "#fff" }}>
      <h1>📋 All Events Created by Admin</h1>
      {events.length === 0 ? (
        <p>No events found.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {events.map((event) => (
            <li
              key={event.id}
              style={{
                border: "1px solid #555",
                padding: "1rem",
                marginBottom: "1rem",
                borderRadius: "8px",
                background: "#2c2c2c",
              }}
            >
              <h2>{event.name}</h2>
              <p><strong>Category:</strong> {event.category}</p>
              <p><strong>Date:</strong> {new Date(event.eventDate).toLocaleString()}</p>
              <p><strong>Description:</strong> {event.description}</p>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
