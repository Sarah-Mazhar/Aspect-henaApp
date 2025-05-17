import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import "./HostDashboard.css";

export default function HostDashboard() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const hostId = localStorage.getItem("hostId");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || role !== "HOST" || !hostId) {
      alert("Invalid session or not a host. Redirecting...");
      navigate("/login");
    }
  }, [token, role, hostId, navigate]);

  const handleCreateEvent = () => {
    navigate(`/host/create-event`);
  };

  const handleViewMyEvents = () => {
    navigate(`/host/events/${hostId}`);
  };

return (
  <div className="host-dashboard">
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
    <div className="dashboard-box">
      <h1>Host Dashboard ✅</h1>
      <p>You are successfully authenticated as a <strong>HOST</strong>.</p>
      <button onClick={handleCreateEvent}>Create New Event</button>
      <button onClick={handleViewMyEvents}>View My Events</button>
    </div>
  </div>
);

}
