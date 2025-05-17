import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import axios from "axios";


export default function AdminProfilePage() {
  const id = localStorage.getItem("adminId"); // ✅ use correct key here
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
        const staticAuth = btoa("admin:adminpass");
        const res = await axios.get(`http://localhost:8080/api/user/${id}`, {
          headers: {
            Authorization: `Basic ${staticAuth}`,
          },
        });
        setAdmin(res.data);
        setForm({ username: res.data.username, email: res.data.email });
      } catch (err) {
        setError("❌ Failed to load admin profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchAdmin();
  }, [id]);

  const handleUpdate = async () => {
    const staticAuth = btoa("admin:adminpass");
    try {
      await axios.put(`http://localhost:8080/api/user/update/${id}`, form, {
        headers: {
          Authorization: `Basic ${staticAuth}`,
          "Content-Type": "application/json",
        },
      });
      setAdmin((prev) => ({ ...prev, ...form }));
      setSuccess("✅ Profile updated successfully.");
      setEditing(false);
    } catch (err) {
      setError("❌ Update failed.");
    }
  };

  if (loading) return <p className="loading">Loading admin profile...</p>;
  if (!admin) return <p className="error-msg">{error || "No admin data found."}</p>;

  return (
    <div className="user-profile-container">
      <Navbar role="ADMIN" />
      <h2>Admin Profile</h2>
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
          <input type="text" value={admin.role} disabled />
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
