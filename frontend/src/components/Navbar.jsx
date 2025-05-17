// src/components/Navbar.jsx
import { useNavigate } from "react-router-dom";
import "./Navbar.css";

export default function Navbar({ userId }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <h2>User Dashboard</h2>
      <div className="nav-buttons">
        <button onClick={() => navigate(`/profile/${userId}`)}>Profile</button>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}
