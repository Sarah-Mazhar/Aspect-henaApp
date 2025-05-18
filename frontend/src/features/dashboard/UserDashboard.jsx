import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUpcomingEvents, rsvpToEvent, cancelRSVP } from "../../services/api";
import "./UserDashboard.css";
import Navbar from "../../components/Navbar";

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
      navigate("/login", { replace: true });
    } else {
      fetchEvents();
    }
  }, []);

  const fetchEvents = async () => {
    try {
      const events = await getUpcomingEvents();
      setEvents(events);
    } catch (err) {
      console.error("❌ Failed to fetch events:", err);
      setError("Failed to load events.");
    } finally {
      setLoading(false);
    }
  };

  const handleToggleRSVP = async (event) => {
    const userIdNum = Number(userId);
    const isUserRSVPed = event.rsvps?.some((u) => Number(u.id) === userIdNum);

    try {
      if (isUserRSVPed) {
        await cancelRSVP({ userId: userIdNum, eventId: event.id });
        setRsvpSuccess(`Canceled RSVP for "${event.name}"`);
      } else {
        await rsvpToEvent({ userId: userIdNum, eventId: event.id });
        setRsvpSuccess(`Successfully RSVP’d to "${event.name}"`);
      }

      fetchEvents();
    } catch (err) {
      console.error("❌ RSVP toggle error:", err);
      alert("Could not process RSVP. Try again.");
    } finally {
      setTimeout(() => setRsvpSuccess(""), 3000);
    }
  };

  return (
    <div className="user-dashboard-container">
      <Navbar userId={userId} />

      <div className="dashboard-box">
        <h2>Upcoming Events 🎉</h2>
        {rsvpSuccess && <p className="success-msg">{rsvpSuccess}</p>}

        {loading ? (
          <p>Loading events...</p>
        ) : error ? (
          <p className="error-msg">{error}</p>
        ) : events.length === 0 ? (
          <p>No upcoming events found.</p>
        ) : (
          <ul className="event-list">
            {events.map((event) => {
              const userIdNum = Number(userId);
              const isUserRSVPed = event.rsvps?.some((u) => Number(u.id) === userIdNum);
              const isFull = event.currentAttendees >= event.maxAttendees;
              const canAttend = isUserRSVPed || !isFull;

              return (
                <li key={event.id} className="event-item">
                  <h3>{event.name}</h3>
                  <p>{event.description}</p>
                  <p><strong>Date:</strong> {event.eventDate ? new Date(event.eventDate).toLocaleString() : "TBA"}</p>
                  <p><strong>Location:</strong> {event.location || "Unknown"}</p>
                  <p><strong>Category:</strong> {event.category || "Uncategorized"}</p>
                  <p><strong>Attendees:</strong> {event.currentAttendees} / {event.maxAttendees}</p>
                  <button
                    onClick={() => handleToggleRSVP(event)}
                    disabled={!canAttend}
                  >
                    {isUserRSVPed ? "Cancel" : isFull ? "Event Full" : "Attend"}
                  </button>
                </li>
              );
            })}
          </ul>
        )}
      </div>
    </div>
  );
}
