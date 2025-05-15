import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createEvent } from "../../services/api";
import "../dashboard/Dashboard.css";

export default function CreateEventPage() {
  const navigate = useNavigate();
  const adminId = localStorage.getItem("adminId");

  const [eventData, setEventData] = useState({
    name: "",
    description: "",
    date: "",
    location: "",
    category: "",
    maxAttendees: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEventData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        ...eventData,
        eventDate: eventData.date + "T00:00:00", // 🧠 Convert to LocalDateTime format
        createdByAdminId: Number(adminId),
        maxAttendees: Number(eventData.maxAttendees),
      };

      const result = await createEvent({
        eventData: payload,
        role: "ADMIN",
        adminId,
      });

      alert(`✅ Event "${result.name}" created successfully!`);
      navigate("/admin-dashboard");
    } catch (error) {
      console.error("❌ Error creating event:", error);
      alert("Failed to create event.");
    }
  };

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-box">
        <h2>Create New Event 🎉</h2>
        <form className="event-form form-grid" onSubmit={handleSubmit}>
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
          <button type="submit" className="create-event-btn">
            ✅ Submit Event
          </button>
        </form>
        <button onClick={() => navigate("/admin-dashboard")} style={{ marginTop: "1rem" }}>
          🔙 Back to Dashboard
        </button>
      </div>
    </div>
  );
}
