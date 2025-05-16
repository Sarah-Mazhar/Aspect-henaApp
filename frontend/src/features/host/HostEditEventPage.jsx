import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";

export default function HostEditEventPage() {
  const { eventId } = useParams();
  const hostId = localStorage.getItem("hostId");
  const [eventData, setEventData] = useState(null);
  const navigate = useNavigate();

  const staticAuthHeader = {
    Authorization: "Basic " + btoa("host:hostpass"), // ✅ Basic Auth
  };

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/event/${eventId}`, {
          headers: staticAuthHeader,
        });
        setEventData({
          ...response.data,
          date: response.data.eventDate?.split("T")[0] || "",
          time: response.data.eventDate?.split("T")[1]?.slice(0, 5) || "",
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

      await axios.put(
        `http://localhost:8080/api/event/update/${hostId}/${eventId}`,
        payload,
        { headers: staticAuthHeader }
      );

      alert("✅ Event updated successfully.");
      navigate(`/host/events/${hostId}`);
    } catch (error) {
      console.error("❌ Error updating event:", error);
      alert("Failed to update event.");
    }
  };

  if (!eventData) return <p>Loading event data...</p>;

  return (
    <div className="edit-event-form">
      <h2>Edit Event ✏️</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="name" value={eventData.name} onChange={handleChange} required />
        <input type="text" name="description" value={eventData.description} onChange={handleChange} required />
        <input type="date" name="date" value={eventData.date} onChange={handleChange} required />
        <input type="time" name="time" value={eventData.time} onChange={handleChange} required />
        <input type="text" name="location" value={eventData.location} onChange={handleChange} required />
        <input type="text" name="category" value={eventData.category} onChange={handleChange} required />
        <input
          type="number"
          name="maxAttendees"
          value={eventData.maxAttendees}
          onChange={handleChange}
          required
          min={1}
        />
        <button type="submit">💾 Save Changes</button>
        <button type="button" onClick={() => navigate(`/host/events/${hostId}`)}>❌ Cancel</button>
      </form>
    </div>
  );
}
