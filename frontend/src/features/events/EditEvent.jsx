import React, { useEffect, useState } from "react";
import axios from "../../api/axiosInstance";
import { useNavigate } from "react-router-dom";
import "../../styles/CreateEvent.css"; // ✅ Reuse the same style

const EditEvent = () => {
    const [form, setForm] = useState(null);
    const navigate = useNavigate();
    const userId = localStorage.getItem("userId");

    useEffect(() => {
        const stored = localStorage.getItem("eventToEdit");
        if (stored) setForm(JSON.parse(stored));
    }, []);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put(`/event/update/${userId}/${form.id}`, form);
            alert("✅ Event updated successfully!");
            navigate("/host-dashboard");
        } catch (error) {
            console.error("❌ Update error:", error);
            alert("❌ Failed to update event.");
        }
    };

    if (!form) return <p className="form-loading">Loading...</p>;

    return (
        <div className="create-event-container">
            <h2>Edit Event</h2>
            <form className="create-event-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="name"
                    placeholder="Name"
                    value={form.name}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="description"
                    placeholder="Description"
                    value={form.description}
                    onChange={handleChange}
                    required
                />
                <input
                    type="datetime-local"
                    name="eventDate"
                    value={form.eventDate}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="location"
                    placeholder="Location"
                    value={form.location}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="category"
                    placeholder="Category"
                    value={form.category}
                    onChange={handleChange}
                    required
                />
                <input
                    type="number"
                    name="maxAttendees"
                    placeholder="Max Attendees"
                    value={form.maxAttendees}
                    onChange={handleChange}
                    required
                />
                <button type="submit">Update</button>
            </form>
        </div>
    );
};

export default EditEvent;
