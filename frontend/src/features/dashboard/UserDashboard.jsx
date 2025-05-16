import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./Dashboard.css"; // Make sure this file exists

export default function UserDashboard() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || role !== "USER" || !userId) {
      alert("Invalid session or not a user. Redirecting...");
      navigate("/login");
    } else {
      fetchUpcomingEvents();
    }
  }, []);

const fetchUpcomingEvents = async () => {
  try {
    const username = "user";
    const password = "userpass";
    const authHeader = btoa(`${username}:${password}`);

    const response = await axios.get("http://localhost:8080/api/event/upcoming", {
      headers: {
        Authorization: `Basic ${authHeader}`,
      },
    });

    console.log("✅ Events fetched:", response.data);
    setEvents(response.data);
  } catch (err) {
    console.error("❌ Failed to fetch events:", err);
    setError("Failed to load events.");
  } finally {
    setLoading(false);
  }
};


  return (
    <div className="dashboard-wrapper">
      <button
        className="logout-btn"
        onClick={() => {
          localStorage.clear();
          navigate("/login");
        }}
      >
        Logout
      </button>

      <div className="dashboard-box">
        <h1>User Dashboard ✅</h1>
        <p>You are successfully authenticated as a <strong>USER</strong>.</p>

        <h2>Upcoming Events 🎉</h2>

        {loading ? (
          <p>Loading events...</p>
        ) : error ? (
          <p style={{ color: "red" }}>{error}</p>
        ) : events.length === 0 ? (
          <p>No upcoming events found.</p>
        ) : (
          <ul className="event-list">
            {events.map((event) => (
              <li key={event.id} className="event-item">
                <h3>{event.name}</h3>
                <p>{event.description}</p>
                <p>
                  <strong>Date:</strong>{" "}
                  {event.eventDate
                    ? new Date(event.eventDate).toLocaleString()
                    : "TBA"}
                </p>
                <p>
                  <strong>Location:</strong> {event.location || "Unknown"}
                </p>
                <p>
                  <strong>Category:</strong> {event.category || "Uncategorized"}
                </p>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
