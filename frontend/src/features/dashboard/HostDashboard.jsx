import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

export default function HostDashboard() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const hostId = localStorage.getItem("hostId");
  const navigate = useNavigate();

  useEffect(() => {
    console.log("🔐 Token:", token);
    console.log("🧑‍💼 Role:", role);
    console.log("🆔 Host ID:", hostId);

    if (!token || role !== "HOST" || !hostId) {
      alert("Invalid session or not a host. Redirecting...");
      navigate("/login");
    }
  }, [token, role, hostId, navigate]);

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
        <h1>Host Dashboard ✅</h1>
        <p>You are successfully authenticated as a <strong>HOST</strong>.</p>
      </div>
    </div>
  );
}
