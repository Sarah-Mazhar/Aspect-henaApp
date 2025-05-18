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
      alert("Could not process RSVP. Try again.");
    } finally {
      setTimeout(() => setRsvpSuccess(""), 3000);
    }
  };

  return (
    <div className="user-dashboard-container">
      <Navbar userId={userId} />

      <div className="dashboard-box">
        <h2 className="dashboard-title">
          <span className="highlight">Upcoming</span> <span className="normal">Events</span>
        </h2>
        {rsvpSuccess && <p className="success-msgg">{rsvpSuccess}</p>}

        {loading ? (
          <p className="status-msgg">Loading events...</p>
        ) : error ? (
          <p className="status-msgg">{error}</p>
        ) : events.length === 0 ? (
          <p className="status-msgg">No upcoming events found.</p>
        ) : (
          <div className="event-grid">
            {events.map((event) => {
              const userIdNum = Number(userId);
              const isUserRSVPed = event.rsvps?.some((u) => Number(u.id) === userIdNum);
              const isFull = event.currentAttendees >= event.maxAttendees;
              const canAttend = isUserRSVPed || !isFull;

              return (
                <div key={event.id} className="event-card">
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
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
