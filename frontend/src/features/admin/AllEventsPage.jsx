import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { FaEdit, FaTrash } from "react-icons/fa";
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
    const authHeader = btoa(`admin:adminpass`);
    try {
      const res = await axios.get(`${API_BASE}/event/admin/${adminId}`, {
        headers: { Authorization: `Basic ${authHeader}` },
      });
      setEvents(res.data);
    } catch (err) {
      console.error("Error fetching events:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (eventId) => {
    if (!window.confirm("Are you sure you want to delete this event?")) return;

    const authHeader = btoa(`admin:adminpass`);
    try {
      await axios.delete(`${API_BASE}/event/delete/${adminId}/${eventId}`, {
        headers: { Authorization: `Basic ${authHeader}` },
      });
      setEvents(events.filter((e) => e.id !== eventId));
    } catch (err) {
      console.error("Error deleting event:", err);
      alert("Failed to delete the event.");
    }
  };

  const handleEdit = (event) => {
    navigate(`/admin/edit-event/${event.id}`, { state: { event } });
  };

  if (loading) return <p className="loading-text">Loading events...</p>;

  return (
    <div className="all-events-wrapper">
      <h1>📋 All Events Created by Admin</h1>
      {events.length === 0 ? (
        <p className="no-events">No events found.</p>
      ) : (
        <ul className="event-list">
          {events.map((event) => (
            <li key={event.id} className="event-card">
              <h2>{event.name}</h2>
              <p><strong>Category:</strong> {event.category}</p>
              <p><strong>Date:</strong> {new Date(event.eventDate).toLocaleString()}</p>
              <p><strong>Description:</strong> {event.description}</p>

              <div className="event-actions">
                <FaEdit
                  onClick={() => handleEdit(event)}
                  className="icon edit-icon"
                  title="Edit"
                />
                <FaTrash
                  onClick={() => handleDelete(event.id)}
                  className="icon delete-icon"
                  title="Delete"
                />
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
