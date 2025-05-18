import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import "./AdminDashboard.css";

export default function AdminDashboard() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const adminId = localStorage.getItem("adminId");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || role !== "ADMIN" || !adminId) {
      alert("Invalid session or not an admin. Redirecting...");
      navigate("/login");
    }
  }, [token, role, adminId, navigate]);

  return (
    <div className="dashboard-wrapper">
      <Navbar role={role} />

      <h1 className="gradient-text dashboard-heading">Admin Dashboard</h1>

      <div className="card-grid centered-grid">
        <div className="glass-card no-header">
          <button onClick={() => navigate("/admin/create-event")}>
             Create Event
          </button>
        </div>

        <div className="glass-card no-header">
          <button onClick={() => navigate("/admin/events")}>
             Show All Events
          </button>
        </div>

        <div className="glass-card no-header">
          <button onClick={() => navigate("/admin/create-user")}>
             Create User
          </button>
        </div>
      </div>
    </div>
  );
}
