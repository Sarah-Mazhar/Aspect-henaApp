import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createEvent } from "../../services/api";
import Navbar from "../../components/Navbar";
import "./CreateEventPage.css";

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
        createdByAdminId: Number(adminId),
        maxAttendees: Number(eventData.maxAttendees),
      };

      const result = await createEvent({
        eventData: payload,
        role: "ADMIN",
        adminId,
      });

      alert(`Event "${result.name}" created successfully!`);
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
      console.error("Error creating event:", error);
      alert("Failed to create event.");
    }
  };

  return (
    <div className="create-event-wrapper">
      <Navbar role="ADMIN" />

  
  <form className="create-event-form" onSubmit={handleSubmit}>
  <h2 className="custom-create-title">
    <span className="pink-text">New</span> <span className="white-text">Event</span>
  </h2>

  <input type="text" name="name" placeholder="Event Name" value={eventData.name} onChange={handleChange} required />
  <input type="text" name="description" placeholder="Description" value={eventData.description} onChange={handleChange} required />
  <input type="date" name="date" value={eventData.date} onChange={handleChange} required />
  <input type="time" name="time" value={eventData.time} onChange={handleChange} />
  <input type="text" name="location" placeholder="Location" value={eventData.location} onChange={handleChange} required />
  <input type="text" name="category" placeholder="Category" value={eventData.category} onChange={handleChange} required />
  <input type="number" name="maxAttendees" placeholder="Max Attendees" min={1} value={eventData.maxAttendees} onChange={handleChange} required />

  <div className="event-buttons">
    <button type="submit">Submit Event</button>
    <button type="button" onClick={() => navigate("/admin-dashboard")}>Cancel</button>
  </div>
</form>
    </div>
  );
}
