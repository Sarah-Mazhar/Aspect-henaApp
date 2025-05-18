import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { createUser } from "../../services/api";
import Navbar from "../../components/Navbar";
import "./CreateUserPage.css";

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
      const response = await createUser({ formData, adminId });
      const createdUsername = response.username;
      const createdRole = response.role;

      const roleMsg =
        createdRole === "ADMIN"
          ? `Admin '${createdUsername}' created with full privileges.`
          : createdRole === "HOST"
          ? `Host '${createdUsername}' can now manage events.`
          : `User '${createdUsername}' created successfully.`;

      setMessage(`✅ ${roleMsg}`);
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
    <div className="create-user-wrapper">
      <Navbar role="ADMIN" />
      <div className="create-user-card">
        <h2 className="create-user-title">Create New User</h2>
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

        <button
          className="cancel-btn-create"
          onClick={() => navigate("/admin-dashboard")}
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
