import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import axios from "axios";
import "./adminProfilePage.css";

export default function AdminProfilePage() {
  const navigate = useNavigate();
  const id = localStorage.getItem("adminId");
  const [admin, setAdmin] = useState(null);
  const [form, setForm] = useState({ username: "", email: "" });
  const [editing, setEditing] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) {
      setError("❌ Missing admin ID.");
      setLoading(false);
      return;
    }

    const fetchAdmin = async () => {
      try {
        const auth = btoa("admin:adminpass");
        const res = await axios.get(`http://localhost:8080/api/user/${id}`, {
          headers: {
            Authorization: `Basic ${auth}`,
          },
        });
        setAdmin(res.data);
        setForm({ username: res.data.username, email: res.data.email });
      } catch {
        setError("❌ Failed to load admin profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchAdmin();
  }, [id]);

  const handleUpdate = async () => {
    try {
      const auth = btoa("admin:adminpass");
      await axios.put(
        `http://localhost:8080/api/user/update/${id}`,
        form,
        {
          headers: {
            Authorization: `Basic ${auth}`,
            "Content-Type": "application/json",
          },
        }
      );
      setAdmin((prev) => ({ ...prev, ...form }));
      setSuccess("✅ Profile updated successfully.");
      setError("");
      setEditing(false);
    } catch {
      setSuccess("");
      setError("❌ Update failed.");
    }
  };

  if (loading) return <p className="loading">Loading admin profile...</p>;
  if (!admin) return <p className="error-msg">{error || "No admin data found."}</p>;

  return (
    <div className="profile-wrapper">
      <Navbar role="ADMIN" />

      {error && <p className="error-msg">{error}</p>}
      {success && <p className="success-msg">{success}</p>}

      <div className="profile-card">
        <h2 className="profile-title">
  <span className="pink-text">Profile</span> <span className="white-text">Details</span>
</h2>


        <input
          type="text"
          value={form.username}
          disabled={!editing}
          onChange={(e) =>
            setForm((prev) => ({ ...prev, username: e.target.value }))
          }
        />

        <input
          type="email"
          value={form.email}
          disabled={!editing}
          onChange={(e) =>
            setForm((prev) => ({ ...prev, email: e.target.value }))
          }
        />

        <input type="text" value={admin.role} disabled />

        <div className="profile-actions">
          {editing ? (
            <button onClick={handleUpdate}>Save</button>
          ) : (
            <button className="full-width-btn" onClick={() => setEditing(true)}>
              Edit
            </button>
          )}
          <button className="cancel-btn" onClick={() => navigate("/admin-dashboard")}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
