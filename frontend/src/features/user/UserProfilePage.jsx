import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import axios from "axios";
import "./UserProfile.css";

export default function UserProfilePage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [form, setForm] = useState({ username: "", email: "" });
  const [editing, setEditing] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/user/${userId}`, {
          headers: {
            Authorization: "Basic " + btoa("user:userpass"),
          },
        });
        setUser(response.data);
        setForm({ username: response.data.username, email: response.data.email });
      } catch (err) {
        setError("❌ Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [userId]);

  const handleUpdate = async () => {
    try {
      await axios.put(
        `http://localhost:8080/api/user/update/${userId}`,
        form,
        {
          headers: {
            Authorization: "Basic " + btoa("user:userpass"),
            "Content-Type": "application/json",
          },
        }
      );
      setUser((prev) => ({ ...prev, ...form }));
      setSuccess("✅ Profile updated successfully.");
      setEditing(false);
      setError("");
    } catch {
      setSuccess("");
      setError("❌ Update failed.");
    }
  };

  if (loading) return <p className="user-loading">Loading user profile...</p>;
  if (!user) return <p className="user-error-msg">{error || "No user data found."}</p>;

  return (
    <div className="user-profile-wrapper">
      <Navbar role="USER" />
      {error && <p className="user-error-msg">{error}</p>}
      {success && <p className="user-success-msg">{success}</p>}

      <div className="user-profile-card">
        <h2 className="user-profile-title">
          <span className="pink-text">Profile</span> <span className="white-text">Details</span>
        </h2>

        <input
          type="text"
          value={form.username}
          disabled={!editing}
          onChange={(e) => setForm((prev) => ({ ...prev, username: e.target.value }))}
        />
        <input
          type="email"
          value={form.email}
          disabled={!editing}
          onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
        />
        <input type="text" value={user.role} disabled />

        <div className="user-profile-actions">
          {editing ? (
            <button onClick={handleUpdate}>Save</button>
          ) : (
            <button onClick={() => setEditing(true)}>Edit</button>
          )}
          <button className="cancel-btn-user" onClick={() => navigate("/user-dashboard")}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
