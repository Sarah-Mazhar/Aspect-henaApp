import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import { useNavigate } from "react-router-dom";
import { getHostProfile, updateHostProfile } from "../../services/api";
import "./HostProfilePage.css";

export default function HostProfilePage() {
  const navigate = useNavigate();
  const hostId = localStorage.getItem("hostId");

  const [host, setHost] = useState(null);
  const [form, setForm] = useState({ username: "", email: "" });
  const [editing, setEditing] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!hostId) {
      setError("Missing host ID.");
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        const data = await getHostProfile(hostId);
        setHost(data);
        setForm({ username: data.username, email: data.email });
      } catch (err) {
        setError("Failed to load host profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [hostId]);

  const handleUpdate = async () => {
    try {
      await updateHostProfile(hostId, form);
      setHost((prev) => ({ ...prev, ...form }));
      setSuccess("Profile updated successfully.");
      setError("");
      setEditing(false);
    } catch (err) {
      setError("Update failed.");
      setSuccess("");
    }
  };

  if (loading) return <p className="loading">Loading host profile...</p>;
  if (!host) return <p className="error-msg">{error || "No host data found."}</p>;

  return (
    <div className="host-profile-wrapper">
      <Navbar role="HOST" />
      {error && <p className="error-msg">{error}</p>}
      {success && <p className="success-msg">{success}</p>}

      <div className="host-profile-card">
        <h2 className="host-profile-title">
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
        <input type="text" value={host.role} disabled />

        <div className="host-profile-actions">
          {editing ? (
            <button onClick={handleUpdate}>Save</button>
          ) : (
            <button onClick={() => setEditing(true)}>Edit</button>
          )}
          <button className="host-cancel-btn" onClick={() => navigate("/host-dashboard")}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
