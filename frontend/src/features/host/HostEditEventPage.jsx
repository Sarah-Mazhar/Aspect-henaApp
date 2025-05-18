import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getEventById, updateEventByHost } from "../../services/api";
import "./HostEditEventPage.css";

export default function HostEditEventPage() {
  const { eventId } = useParams();
  const hostId = localStorage.getItem("hostId");
  const navigate = useNavigate();
  const [eventData, setEventData] = useState(null);

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const data = await getEventById(eventId);
        setEventData({
          ...data,
          date: data.eventDate?.split("T")[0] || "",
          time: data.eventDate?.split("T")[1]?.slice(0, 5) || "",
        });
      } catch (error) {
        console.error("Failed to fetch event:", error);
        alert("Event not found or access denied.");
        navigate("/host-dashboard");
      }
    };

    fetchEvent();
  }, [eventId, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEventData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...eventData,
        eventDate: `${eventData.date}T${eventData.time}:00`,
      };

      await updateEventByHost(hostId, eventId, payload);

      alert("Event updated successfully.");
      navigate(`/host/events/${hostId}`);
    } catch (error) {
      console.error("Error updating event:", error);
      alert("Failed to update event.");
    }
  };

  if (!eventData) {
    return <p className="host-loading-text">Loading event data...</p>;
  }

  return (
    <div className="host-edit-wrapper">
      <form className="host-edit-form-box host-edit-form" onSubmit={handleSubmit}>
        <h2 className="host-edit-title">
          <span className="pink-text">Edit</span> <span className="white-text">Event</span>
        </h2>

        <input
          type="text"
          name="name"
          value={eventData.name}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="description"
          value={eventData.description}
          onChange={handleChange}
          required
        />
        <input
          type="date"
          name="date"
          value={eventData.date}
          onChange={handleChange}
          required
        />
        <input
          type="time"
          name="time"
          value={eventData.time}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="location"
          value={eventData.location}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="category"
          value={eventData.category}
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="maxAttendees"
          value={eventData.maxAttendees}
          onChange={handleChange}
          required
          min={1}
        />

        <div className="host-edit-buttons">
          <button type="submit">Save Changes</button>
          <button type="button" onClick={() => navigate(`/host/events/${hostId}`)}>
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
