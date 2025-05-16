import React, { useState } from "react";
import axios from "../../api/axiosInstance";
import { useNavigate } from "react-router-dom";
import "../../styles/CreateEvent.css"; // ✅ Make sure this path is correct

const CreateEvent = () => {
    const [formData, setFormData] = useState({
        name: "",
        description: "",
        eventDate: "",
        location: "",
        category: ""
    });

    const userId = localStorage.getItem("userId");
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post(`/event/create/${userId}`, formData);
            alert("✅ Event created successfully!");
            navigate("/host-dashboard");
        } catch (error) {
            console.error("❌ Failed to create event", error);
            alert("❌ Could not create event.");
        }
    };

    return (
        <div className="create-event-container">
            <h2>Create New Event</h2>
            <form className="create-event-form" onSubmit={handleSubmit}>
                <input type="text" name="name" placeholder="Name" value={formData.name} onChange={handleChange} required />
                <input type="text" name="description" placeholder="Description" value={formData.description} onChange={handleChange} required />
                <input type="datetime-local" name="eventDate" value={formData.eventDate} onChange={handleChange} required />
                <input type="text" name="location" placeholder="Location" value={formData.location} onChange={handleChange} required />
                <input type="text" name="category" placeholder="Category" value={formData.category} onChange={handleChange} required />
                <button type="submit">Create</button>
            </form>
        </div>
    );
};

export default CreateEvent;
