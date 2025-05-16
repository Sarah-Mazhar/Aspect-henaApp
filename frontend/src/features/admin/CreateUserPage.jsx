import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./CreateUserPage.css";

const API_BASE = "http://localhost:8080/api";

export default function CreateUserPage() {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const adminId = localStorage.getItem("adminId");

  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    role: "USER",
  });

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // Redirect if not authenticated or not admin
  useEffect(() => {
    if (!token || role !== "ADMIN" || !adminId) {
      alert("Unauthorized access. Redirecting to login...");
      navigate("/login");
    }
  }, [token, role, adminId, navigate]);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

    const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setLoading(true);

    try {
        const staticCreds = {
        ADMIN: { username: "admin", password: "adminpass" },
        };
        const authHeader = btoa(
        `${staticCreds.ADMIN.username}:${staticCreds.ADMIN.password}`
        );

        const response = await axios.post(
        `${API_BASE}/admin/createUser/${adminId}`,
        formData,
        {
            headers: {
            Authorization: `Basic ${authHeader}`,
            },
        }
        );

        const createdUsername = response.data.username;
        const createdRole = response.data.role;

        let roleMsg = "";
        switch (createdRole) {
        case "ADMIN":
            roleMsg = `🛡️ Admin '${createdUsername}' created with full privileges.`;
            break;
        case "HOST":
            roleMsg = `🎙️ Host '${createdUsername}' can now manage events.`;
            break;
        default:
            roleMsg = `👤 User '${createdUsername}' created successfully.`;
            break;
        }

        setMessage(`✅ ${roleMsg}`);

        // Reset form
        setFormData({
        username: "",
        email: "",
        password: "",
        role: "USER",
        });

    } catch (error) {
        const errMsg =
        error?.response?.data?.message || "❌ Failed to create user.";
        setMessage(errMsg);
    } finally {
        setLoading(false);
    }
    };

  return (
    <div className="create-user-container">
      <div className="create-user-form-wrapper">
        <button className="back-btn" onClick={() => navigate("/admin-dashboard")}>
          ⬅ Back to Dashboard
        </button>
        <h2>Create New User</h2>
        <form className="create-user-form" onSubmit={handleSubmit}>
          <input
            name="username"
            placeholder="Username"
            value={formData.username}
            onChange={handleChange}
            required
          />
          <input
            name="email"
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required
          />
          <select name="role" value={formData.role} onChange={handleChange}>
            <option value="USER">USER</option>
            <option value="HOST">HOST</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <button type="submit" disabled={loading}>
            {loading ? "Creating..." : "Create User"}
          </button>
        </form>
        {message && <div className="feedback-msg">{message}</div>}
      </div>
    </div>
  );
}
