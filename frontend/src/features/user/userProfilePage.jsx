import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getUserProfile } from "../../services/api";
import axios from "axios";


export default function UserProfilePage() {
  const { userId } = useParams();
  const [user, setUser] = useState(null);
  const [form, setForm] = useState({ username: "", email: "" });
  const [editing, setEditing] = useState(false);
  const [events, setEvents] = useState([]);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const data = await getUserProfile(userId);
        setUser({
          id: data.id,
          username: data.username,
          email: data.email,
          role: data.role,
        });
        setForm({ username: data.username, email: data.email });
        setEvents(data.events || []);
      } catch (err) {
        setError("❌ Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [userId]);

  const handleUpdate = async () => {
    const staticAuth = btoa("user:userpass");
    try {
      await axios.put(
        `http://localhost:8080/api/user/update/${userId}`,
        form,
        {
          headers: {
            Authorization: `Basic ${staticAuth}`,
            "Content-Type": "application/json",
          },
        }
      );
      setUser((prev) => ({ ...prev, ...form }));
      setSuccess("✅ Profile updated successfully.");
      setEditing(false);
    } catch (err) {
      setError("❌ Update failed.");
    }
  };

  if (loading) return <p className="loading">Loading...</p>;

  return (
    <div className="user-profile-container">
      <h2>User Profile</h2>
      {error && <p className="error-msg">{error}</p>}
      {success && <p className="success-msg">{success}</p>}

      <div className="profile-form">
        <label>
          Username:
          <input
            type="text"
            value={form.username}
            disabled={!editing}
            onChange={(e) =>
              setForm((prev) => ({ ...prev, username: e.target.value }))
            }
          />
        </label>

        <label>
          Email:
          <input
            type="email"
            value={form.email}
            disabled={!editing}
            onChange={(e) =>
              setForm((prev) => ({ ...prev, email: e.target.value }))
            }
          />
        </label>

        <label>
          Role:
          <input type="text" value={user.role} disabled />
        </label>

        <div className="profile-buttons">
          {editing ? (
            <>
              <button onClick={handleUpdate}>Save</button>
              <button onClick={() => setEditing(false)}>Cancel</button>
            </>
          ) : (
            <button onClick={() => setEditing(true)}>Edit</button>
          )}
        </div>
      </div>

      <div className="attending-events">
        <h3>Events You're Attending 🎉</h3>
        {events.length === 0 ? (
          <p style={{ color: "#888" }}>No RSVP’d events yet.</p>
        ) : (
          <ul className="event-list">
            {events.map((event) => (
              <li key={event.id} className="event-item">
                <h4>{event.name}</h4>
                <p>
                  <strong>Date:</strong>{" "}
                  {new Date(event.eventDate).toLocaleString()}
                </p>
                <p>
                  <strong>Location:</strong> {event.location}
                </p>
                <p>
                  <strong>Category:</strong> {event.category}
                </p>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
