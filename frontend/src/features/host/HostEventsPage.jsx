import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getHostEvents, deleteEvent } from "../../services/api";

export default function HostEventsPage() {
  const hostId = localStorage.getItem("hostId");
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const fetchEvents = async () => {
    try {
      const data = await getHostEvents(hostId);
      setEvents(data);
    } catch (err) {
      console.error("❌ Error fetching events:", err);
      setError("Failed to load events.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (hostId) fetchEvents();
    else setError("Host ID not found.");
  }, [hostId]);

  const handleEdit = (eventId) => {
    navigate(`/host/edit-event/${eventId}`);
  };

  const handleDelete = async (eventId) => {
    if (!window.confirm("Are you sure you want to delete this event?")) return;

    try {
      await deleteEvent({ eventId, hostId });
      alert("✅ Event deleted successfully.");
      fetchEvents(); // Refresh list
    } catch (err) {
      console.error("❌ Error deleting event:", err);
      alert("Failed to delete event.");
    }
  };

  return (
    <div>
      <h2>Events Created by You (Host)</h2>

      {loading ? (
        <p>Loading events...</p>
      ) : error ? (
        <p style={{ color: "red" }}>{error}</p>
      ) : events.length === 0 ? (
        <p>No events found.</p>
      ) : (
        <ul>
          {events.map((event) => (
            <li key={event.id}>
              <strong>{event.name}</strong> –{" "}
              {new Date(event.eventDate).toLocaleString()} at {event.location}
              <br />
              <button onClick={() => handleEdit(event.id)}>✏️ Edit</button>{" "}
              <button onClick={() => handleDelete(event.id)}>🗑️ Delete</button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
