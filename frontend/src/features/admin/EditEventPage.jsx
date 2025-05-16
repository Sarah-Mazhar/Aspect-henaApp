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
    eventDate: event?.eventDate || "",
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
    <div style={{ padding: "2rem", color: "#fff" }}>
      <h1>🛠️ Edit Event</h1>
      <form
        onSubmit={handleSubmit}
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "1rem",
          maxWidth: "400px",
        }}
      >
        <input
          name="name"
          value={form.name}
          onChange={handleChange}
          placeholder="Name"
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
        <button type="submit">Update Event</button>
      </form>
    </div>
  );
}
