import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

export default function UserDashboard() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate();

  useEffect(() => {
    console.log("🔐 Token:", token);
    console.log("🧑‍💼 Role:", role);
    console.log("🆔 User ID:", userId);

    if (!token || role !== "USER" || !userId) {
      alert("Invalid session or not a user. Redirecting...");
      navigate("/login");
    }
  }, [token, role, userId, navigate]);

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
        <h1>User Dashboard ✅</h1>
        <p>You are successfully authenticated as a <strong>USER</strong>.</p>
      </div>
    </div>
  );
}
