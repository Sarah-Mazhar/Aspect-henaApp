import React, { useEffect, useState } from "react";
import axios from "../../api/axiosInstance";
import { useNavigate } from "react-router-dom";
import "../../styles/HostDasboard.css"

const HostDashboard = () => {
    const [events, setEvents] = useState([]);
    const userId = localStorage.getItem("userId");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchEvents = async () => {
            try {
                const response = await axios.get(`/event/host/${userId}`);
                setEvents(Array.isArray(response.data) ? response.data : []);
            } catch (error) {
                console.error("Failed to load events", error);
                setEvents([]);
            }
        };

        fetchEvents();
    }, [userId]);

    const handleEdit = (event) => {
        localStorage.setItem("eventToEdit", JSON.stringify(event));
        navigate("/edit-event");
    };

    const handleDelete = async (eventId) => {
        if (!window.confirm("Are you sure you want to delete this event?")) return;

        try {
            await axios.delete(`/event/delete/${userId}/${eventId}`);
            setEvents(events.filter((e) => e.id !== eventId));
            alert("✅ Event deleted successfully!");
        } catch (error) {
            console.error("❌ Failed to delete event:", error);
            alert("❌ Failed to delete event.");
        }
    };

    return (
        <div className="host-dashboard">
            <h2 className="dashboard-title">Welcome, Host!</h2>
            <h3>Your Events</h3>

            <button className="create-btn" onClick={() => navigate("/create-event")}>
                + Create New Event
            </button>

            {events.length === 0 ? (
                <p className="no-events">No events found.</p>
            ) : (
                <div className="event-list">
                    {events.map((event) => (
                        <div className="event-card" key={event.id}>
                            <h4>{event.name}</h4>
                            <p>{event.eventDate}</p>
                            <div className="event-actions">
                                <button className="edit-btn" onClick={() => handleEdit(event)}>Edit</button>
                                <button className="delete-btn" onClick={() => handleDelete(event.id)}>Delete</button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default HostDashboard;
