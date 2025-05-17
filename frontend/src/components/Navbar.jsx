import { useNavigate } from "react-router-dom";
import "./Navbar.css";

export default function Navbar({ userId, role = "USER" }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const handleProfileClick = () => {
    if (role === "ADMIN") {
      navigate(`/admin/profile/${userId}`);
    } else {
      navigate(`/profile/${userId}`);
    }
  };

  return (
    <nav className="navbar">
      <h2>{role === "ADMIN" ? "Admin Dashboard" : "User Dashboard"}</h2>
      <div className="nav-buttons">
        <button onClick={handleProfileClick}>Profile</button>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}
