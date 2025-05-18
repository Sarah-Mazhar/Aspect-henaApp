import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Navbar from "../../components/Navbar";
import "./AllEventsPage.css";

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
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    const authHeader = btoa("admin:adminpass");
    try {
      const res = await axios.get(`${API_BASE}/event/admin/${adminId}`, {
        headers: { Authorization: `Basic ${authHeader}` },
      });
      setEvents(res.data);
    } catch (err) {
      console.error("Error fetching events:", err);
      alert("Failed to load events.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (eventId) => {
    if (!window.confirm("Are you sure you want to delete this event?")) return;

    const authHeader = btoa("admin:adminpass");
    try {
      await axios.delete(`${API_BASE}/event/delete/${adminId}/${eventId}`, {
        headers: { Authorization: `Basic ${authHeader}` },
      });
      setEvents((prev) => prev.filter((e) => e.id !== eventId));
    } catch (err) {
      console.error("Error deleting event:", err);
      alert("Failed to delete event.");
    }
  };

  const handleEdit = (event) => {
    navigate(`/admin/edit-event/${event.id}`, { state: { event } });
  };

  if (loading) return <p className="loading-text">Loading events...</p>;

  return (
    <div className="all-events-wrapper">
      <Navbar role="ADMIN" />
      <div className="all-events-container">
        <h2 className="events-title">📋 All Events Created by Admin</h2>

        {events.length === 0 ? (
          <p className="no-events-text">No events found.</p>
        ) : (
          <ul className="events-list">
            {events.map((event) => (
              <li className="event-card" key={event.id}>
                <h3>{event.name}</h3>
                <p><strong>Category:</strong> {event.category}</p>
                <p><strong>Date:</strong> {new Date(event.eventDate).toLocaleString()}</p>
                <p><strong>Description:</strong> {event.description}</p>

                <div className="event-actions">
                  <button className="btn edit-btn" onClick={() => handleEdit(event)}>
                    ✏️ Edit
                  </button>
                  <button className="btn delete-btn" onClick={() => handleDelete(event.id)}>
                    🗑️ Delete
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
