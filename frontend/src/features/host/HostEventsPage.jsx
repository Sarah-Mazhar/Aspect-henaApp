import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getHostEvents, deleteEvent } from "../../services/api";
import { FaEdit, FaTrash } from "react-icons/fa";
import Navbar from "../../components/Navbar";
import "./HostEventsPage.css";

export default function HostEventsPage() {
  const hostId = localStorage.getItem("hostId");
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!hostId) {
      setError("Host ID not found.");
      setLoading(false);
      return;
    }

    const fetchEvents = async () => {
      try {
        const data = await getHostEvents(hostId);
        setEvents(data);
        setFilteredEvents(data);
      } catch (err) {
        console.error("Error fetching events:", err);
        setError("Failed to load events.");
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [hostId]);

  const handleDelete = async (eventId) => {
    if (!window.confirm("Are you sure you want to delete this event?")) return;

    try {
      await deleteEvent({ eventId, hostId });
      const updated = events.filter((e) => e.id !== eventId);
      setEvents(updated);
      setFilteredEvents(updated);
      alert("Event deleted successfully.");
    } catch (err) {
      console.error("Error deleting event:", err);
      alert("Failed to delete event.");
    }
  };

  const handleEdit = (event) => {
    navigate(`/host/edit-event/${event.id}`, { state: { event } });
  };

  const handleSearchChange = (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);

    const results = events.filter((event) =>
      event.name?.toLowerCase().includes(query)
    );

    setFilteredEvents(results);
  };

  return (
    <div className="host-events-wrapper">
      <Navbar role="HOST" />
      <h2 className="host-events-title">
        <span className="host-highlight">Your</span>{" "}
        <span className="host-normal">Events</span>
      </h2>

      <input
        type="text"
        placeholder="Search Events..."
        value={searchQuery}
        onChange={handleSearchChange}
        className="event-search-input"
      />

      {loading ? (
        <p className="host-loading-text">Loading events...</p>
      ) : error ? (
        <p className="host-loading-text" style={{ color: "red" }}>{error}</p>
      ) : filteredEvents.length === 0 ? (
        <p className="host-loading-text">No events found.</p>
      ) : (
        <div className="host-events-grid">
          {filteredEvents.map((event) => (
            <div className="host-event-card" key={event.id}>
              <div className="host-event-card-header">
                <FaEdit
                  className="host-icon-btn"
                  onClick={() => handleEdit(event)}
                />
                <FaTrash
                  className="host-icon-btn host-delete"
                  onClick={() => handleDelete(event.id)}
                />
              </div>
              <div className="host-event-name">{event.name}</div>
              <div className="host-event-info">
                <p>
                  <strong>Category:</strong> {event.category}
                </p>
                <p>
                  <strong>Date:</strong>{" "}
                  {new Date(event.eventDate).toLocaleString()}
                </p>
                <p>
                  <strong>Description:</strong> {event.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
