import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

export default function AdminDashboard() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const adminId = localStorage.getItem("adminId");
  const navigate = useNavigate();

  useEffect(() => {
    console.log("🔐 Token:", token);
    console.log("🧑‍💼 Role:", role);
    console.log("🆔 Admin ID:", adminId);

    if (!token || role !== "ADMIN" || !adminId) {
      alert("Invalid session or not an admin. Redirecting...");
      navigate("/login");
    }
  }, [token, role, adminId, navigate]);

  return (
    <div className="dashboard-wrapper">
      <button
        className="logout-btn"
        onClick={() => {
          localStorage.clear();
          navigate("/login");
        }}
      >
        Logout
      </button>

      <div className="dashboard-box">
        <h1>Admin Dashboard ✅</h1>
        <p>You are successfully authenticated as a <strong>ADMIN</strong>.</p>

        <div className="admin-actions">
          <button onClick={() => navigate("/admin/create-event")}>🎉 Create Event</button>
          <button onClick={() => navigate("/admin/events")}>📋 Show All Events</button>
          <button onClick={() => navigate("/admin/create-user")}>👤 Create User</button>
        </div>
      </div>
    </div>
  );
}
