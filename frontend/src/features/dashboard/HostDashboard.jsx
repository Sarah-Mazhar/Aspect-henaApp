import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import "./HostDashboard.css";
import { FaCalendarPlus, FaCalendarAlt } from "react-icons/fa";


export default function HostDashboard() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const hostId = localStorage.getItem("hostId");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || role !== "HOST" || !hostId) {
      navigate("/login", { replace: true });
    }
  }, [token, role, hostId, navigate]);

  return (
    <div className="host-dashboard">
    
        <h1 className="custom-title">Elevating Events For A Smarter Tomorrow</h1>
      <Navbar role="HOST" userId={hostId} />

      <button
        className="logout-btn"
        onClick={() => {
          localStorage.clear();
          navigate("/login");
        }}
      >
        Logout
      </button>

<div className="card-grid">
  <div className="glass-card" onClick={() => navigate("/host/create-event")}>
    <div className="card-content-vertical">
      <FaCalendarPlus className="card-icon-vertical" />
      <span>Create Event</span>
    </div>
  </div>
  <div className="glass-card" onClick={() => navigate(`/host/events/${hostId}`)}>
    <div className="card-content-vertical">
      <FaCalendarAlt className="card-icon-vertical" />
      <span>My Events</span>
    </div>
  </div>
</div>



    </div>
  );
}
