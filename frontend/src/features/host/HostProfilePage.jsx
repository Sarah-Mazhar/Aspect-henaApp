import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import axios from "axios";


export default function HostProfilePage() {
  const id = localStorage.getItem("hostId");
  const [host, setHost] = useState(null);
  const [form, setForm] = useState({ username: "", email: "" });
  const [editing, setEditing] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) {
      setError("❌ Missing host ID.");
      setLoading(false);
      return;
    }

    const fetchHost = async () => {
      try {
        const staticAuth = btoa("host:hostpass");
        const res = await axios.get(`http://localhost:8080/api/user/${id}`, {
          headers: {
            Authorization: `Basic ${staticAuth}`,
          },
        });
        setHost(res.data);
        setForm({ username: res.data.username, email: res.data.email });
      } catch (err) {
        setError("❌ Failed to load host profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchHost();
  }, [id]);

  const handleUpdate = async () => {
    const staticAuth = btoa("host:hostpass");
    try {
      await axios.put(`http://localhost:8080/api/user/update/${id}`, form, {
        headers: {
          Authorization: `Basic ${staticAuth}`,
          "Content-Type": "application/json",
        },
      });
      setHost((prev) => ({ ...prev, ...form }));
      setSuccess("✅ Profile updated successfully.");
      setEditing(false);
    } catch (err) {
      setError("❌ Update failed.");
    }
  };

  if (loading) return <p className="loading">Loading host profile...</p>;
  if (!host) return <p className="error-msg">{error || "No host data found."}</p>;

  return (
    <div className="user-profile-container">
      <Navbar role="HOST" />
      <h2>Host Profile</h2>
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
          <input type="text" value={host.role} disabled />
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
    </div>
  );
}
