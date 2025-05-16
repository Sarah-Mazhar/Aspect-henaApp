import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUpcomingEvents, rsvpToEvent } from "../../services/api"; // ✅ updated import
import "./Dashboard.css";

export default function UserDashboard() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [rsvpSuccess, setRsvpSuccess] = useState("");

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || role !== "USER" || !userId) {
      alert("Invalid session or not a user. Redirecting...");
      navigate("/login");
    } else {
      fetchEvents();
    }
  }, []);

  const fetchEvents = async () => {
    try {
      const events = await getUpcomingEvents(); // ✅ uses API helper
      setEvents(events);
    } catch (err) {
      console.error("❌ Failed to fetch events:", err);
      setError("Failed to load events.");
    } finally {
      setLoading(false);
    }
  };

  const handleRSVP = async (eventId, eventName) => {
    try {
      await rsvpToEvent({ userId, eventId }); // ✅ uses new RSVP API call
      setRsvpSuccess(`Successfully RSVP’d to "${eventName}"`);
      fetchEvents(); // refresh
    } catch (err) {
      console.error("❌ RSVP error:", err);
      alert("Could not RSVP. The event may be full or an error occurred.");
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
        {rsvpSuccess && <p style={{ color: "green" }}>{rsvpSuccess}</p>}

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
                <p>
                  <strong>Attendees:</strong> {event.currentAttendees} / {event.maxAttendees}
                </p>
                <button
                  onClick={() => handleRSVP(event.id, event.name)}
                  disabled={event.currentAttendees >= event.maxAttendees}
                >
                  {event.currentAttendees >= event.maxAttendees ? "Event Full" : "Attend"}
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
