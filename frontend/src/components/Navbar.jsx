import { FaSignOutAlt, FaUserEdit } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import "./Navbar.css";

export default function Navbar({ role }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const handleUpdateProfile = () => {
    navigate("/admin/profile");
  };

  return (
    <div className="navbar">
      <div className="navbar-left">
        🎯 <strong>{role} Panel</strong>
      </div>

      <div className="navbar-right">
        <div className="icon-wrapper" title="Profile">
          <FaUserEdit className="nav-icon" onClick={handleUpdateProfile} />
        </div>
        <div className="icon-wrapper" title="Logout">
          <FaSignOutAlt className="nav-icon" onClick={handleLogout} />
        </div>
      </div>
    </div>
  );
}
