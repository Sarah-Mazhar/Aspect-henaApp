import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createEvent } from "../../services/api";
import "../dashboard/Dashboard.css";

export default function CreateEventPage() {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const adminId = localStorage.getItem("adminId");

  const [eventData, setEventData] = useState({
    name: "",
    description: "",
    date: "",
    time: "",
    location: "",
    category: "",
    maxAttendees: "",
  });

  // 🔐 Admin authentication check
  useEffect(() => {
    if (!token || role !== "ADMIN" || !adminId) {
      alert("Unauthorized access. Please log in as Admin.");
      navigate("/login");
    }
  }, [token, role, adminId, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEventData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      !eventData.name ||
      !eventData.description ||
      !eventData.date ||
      !eventData.location ||
      !eventData.category ||
      !eventData.maxAttendees
    ) {
      alert("Please fill in all required fields.");
      return;
    }

    try {
      const payload = {
        ...eventData,
        eventDate: `${eventData.date}T${eventData.time || "00:00"}:00`,
        createdByAdminId: Number(adminId),
        maxAttendees: Number(eventData.maxAttendees),
      };

      const result = await createEvent({
        eventData: payload,
        role: "ADMIN",
        adminId,
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
          <button type="submit" className="create-event-btn">
            ✅ Submit Event
          </button>
        </form>
        <button
          onClick={() => navigate("/admin-dashboard")}
          style={{ marginTop: "1rem" }}
        >
          🔙 Back to Dashboard
        </button>
      </div>
    </div>
  );
}
