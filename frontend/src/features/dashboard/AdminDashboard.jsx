import { useEffect } from "react";
import { FaCalendarPlus, FaCalendarAlt, FaUserPlus } from "react-icons/fa";
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
      navigate("/login", { replace: true });
    }
  }, [token, role, adminId, navigate]);

  return (
    <div className="admin-bg">
      <Navbar role={role} />
      <div className="dashboard-wrapper">
        <h1 className="custom-title">Elevating Events For A Smarter Tomorrow</h1>

        <div className="card-grid centered-grid clickable-grid">
          <div
            className="glass-card no-header"
            onClick={() => navigate("/admin/create-event")}
          >
            <div className="card-content">
              <FaCalendarPlus className="card-icon" />
              <span>Create Event</span>
            </div>
          </div>

          <div
            className="glass-card no-header"
            onClick={() => navigate("/admin/events")}
          >
            <div className="card-content">
              <FaCalendarAlt className="card-icon" />
              <span>Show All Events</span>
            </div>
          </div>

          <div
            className="glass-card no-header"
            onClick={() => navigate("/admin/create-user")}
          >
            <div className="card-content">
              <FaUserPlus className="card-icon" />
              <span>Create User</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
