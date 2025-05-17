import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import axios from "axios";
import "./EditEventPage.css";

const API_BASE = "http://localhost:8080/api";

export default function EditEventPage() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const { event } = state || {};

  const [form, setForm] = useState({
    name: event?.name || "",
    category: event?.category || "",
    description: event?.description || "",
    eventDate: event?.eventDate
      ? new Date(event.eventDate).toISOString().slice(0, 16)
      : "",
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const authHeader = btoa(`admin:adminpass`);
    try {
      await axios.put(
        `${API_BASE}/event/update/${event.host.id}/${event.id}`,
        form,
        {
          headers: { Authorization: `Basic ${authHeader}` },
        }
      );
      alert("✅ Event updated successfully!");
      navigate("/admin/events");
    } catch (err) {
      console.error("Error updating event:", err);
      alert("❌ Failed to update event.");
    }
  };

  return (
    <div className="edit-event-container">
      <h2>🛠️ Edit Event</h2>
      <form className="edit-event-form" onSubmit={handleSubmit}>
        <input
          name="name"
          value={form.name}
          onChange={handleChange}
          placeholder="Event Name"
          required
        />
        <input
          name="category"
          value={form.category}
          onChange={handleChange}
          placeholder="Category"
          required
        />
        <input
          name="eventDate"
          type="datetime-local"
          value={form.eventDate}
          onChange={handleChange}
          required
        />
        <textarea
          name="description"
          value={form.description}
          onChange={handleChange}
          placeholder="Description"
          rows={3}
          required
        />
        <button type="submit">✅ Update Event</button>
        <button
          type="button"
          onClick={() => navigate("/admin/events")}
          style={{ marginTop: "1rem", backgroundColor: "#444", color: "#fff" }}
        >
          🔙 Cancel
        </button>
      </form>
    </div>
  );
}
