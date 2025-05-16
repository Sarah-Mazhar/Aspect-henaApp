import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createEvent } from "../../services/api";
import "../admin/CreateEventPage.css";


export default function HostCreateEventPage() {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const hostId = localStorage.getItem("hostId");

  const [eventData, setEventData] = useState({
    name: "",
    description: "",
    date: "",
    time: "",
    location: "",
    category: "",
    maxAttendees: "",
  });

  useEffect(() => {
    if (!token || role !== "HOST" || !hostId) {
      alert("Unauthorized access. Please log in as Host.");
      navigate("/login");
    }
  }, [token, role, hostId, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEventData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const requiredFields = ["name", "description", "date", "location", "category", "maxAttendees"];
    for (const field of requiredFields) {
      if (!eventData[field]) {
        alert("Please fill in all required fields.");
        return;
      }
    }

    try {
      const payload = {
        ...eventData,
        eventDate: `${eventData.date}T${eventData.time || "00:00"}:00`,
        createdByHostId: Number(hostId),
        maxAttendees: Number(eventData.maxAttendees),
      };

      const result = await createEvent({
        eventData: payload,
        role: "HOST",
        adminId: hostId, // using same parameter name for endpoint consistency
      });

      alert(`✅ Event "${result.name}" created successfully!`);
      setEventData({
        name: "",
        description: "",
        date: "",
        time: "",
        location: "",
        category: "",
        maxAttendees: "",
      });
      navigate("/host-dashboard");
    } catch (error) {
      console.error("❌ Error creating event:", error);
      alert("Failed to create event.");
    }
  };

  return (
    <div className="create-event-container">
      <h2>Create New Event 🎉</h2>
      <form className="create-event-form" onSubmit={handleSubmit}>
        <input
          type="text"
          name="name"
          placeholder="Event Name"
          value={eventData.name}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="description"
          placeholder="Description"
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
        />
        <input
          type="text"
          name="location"
          placeholder="Location"
          value={eventData.location}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="category"
          placeholder="Category"
          value={eventData.category}
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="maxAttendees"
          placeholder="Max Attendees"
          value={eventData.maxAttendees}
          onChange={handleChange}
          required
          min={1}
        />
        <button type="submit">✅ Submit Event</button>
        <button
          type="button"
          onClick={() => navigate("/host-dashboard")}
          style={{ marginTop: "1rem", backgroundColor: "#444", color: "#fff" }}
        >
          🔙 Cancel
        </button>
      </form>
    </div>
  );
}
