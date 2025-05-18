import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import { getAllEventsForAdmin, deleteEvent } from "../../services/api";
import { FaEdit, FaTrash } from "react-icons/fa";
import "./AllEventsPage.css";

export default function AllEventsPage() {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]); // ✅ added
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
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
    try {
      const data = await getAllEventsForAdmin();
      setEvents(data);
      setFilteredEvents(data); // ✅ initial filtered list = all
    } catch (err) {
      console.error("Error fetching events:", err);
      alert("Failed to load events.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (eventId) => {
    if (!window.confirm("Are you sure you want to delete this event?")) return;

    try {
      await deleteEvent({ eventId, hostId: adminId, role: "ADMIN" });
      const updated = events.filter((e) => e.id !== eventId);
      setEvents(updated);
      setFilteredEvents(updated); // ✅ update filtered list as well
    } catch (err) {
      console.error("Error deleting event:", err);
      alert("Failed to delete event.");
    }
  };

  const handleEdit = (event) => {
    navigate(`/admin/edit-event/${event.id}`, { state: { event } });
  };

  const handleSearchChange = (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);
    const matches = events.filter((event) =>
      event.name?.toLowerCase().includes(query)
    );
    setFilteredEvents(matches);
  };

  if (loading) return <p className="loading-text">Loading events...</p>;

  return (
    <div className="all-events-wrapper">
      <Navbar role="ADMIN" />
      <h2 className="browse-events-title">
        <span className="browse-text">Browse</span>{" "}
        <span className="events-text">Events</span>
      </h2>

      <input
        type="text"
        placeholder="Search Events..."
        value={searchQuery}
        onChange={handleSearchChange}
        className="event-search-input"
      />

      <div className="events-grid">
        {filteredEvents.map((event) => (
          <div className="event-card-box" key={event.id}>
            <div className="event-card-header">
              <FaEdit className="icon-btn" onClick={() => handleEdit(event)} />
              <FaTrash className="icon-btn delete" onClick={() => handleDelete(event.id)} />
            </div>
            <div className="event-name">{event.name}</div>
            <div className="event-info">
              <p><strong>Category:</strong> {event.category}</p>
              <p><strong>Date:</strong> {new Date(event.eventDate).toLocaleString()}</p>
              <p><strong>Description:</strong> {event.description}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
