import { useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { FaUserCircle, FaSignOutAlt } from "react-icons/fa";
import { fetchUserNotifications } from "../services/api";
import "./Navbar.css";

export default function Navbar({ userId, role = "USER" }) {
  const navigate = useNavigate();
  const [popupNotif, setPopupNotif] = useState(null);
  const lastSeenRef = useRef(Date.now());

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const handleProfileClick = () => {
    if (role === "ADMIN") navigate(`/admin/profile/${userId}`);
    else if (role === "HOST") navigate(`/host/profile/${userId}`);
    else navigate(`/profile/${userId}`);
  };

  useEffect(() => {
    const interval = setInterval(fetchNewNotification, 3000);
    return () => clearInterval(interval);
  }, []);

  const fetchNewNotification = async () => {
    try {
      const notifications = await fetchUserNotifications(userId);

      const newNotif = notifications.find(
        (n) => new Date(n.timestamp).getTime() > lastSeenRef.current
      );

      if (newNotif) {
        lastSeenRef.current = new Date(newNotif.timestamp).getTime();
        setPopupNotif(newNotif);
        setTimeout(() => setPopupNotif(null), 4000);
      }
    } catch (err) {
      console.error("❌ Failed to fetch notifications:", err);
    }
  };

  return (
    <nav className="navbar">
      <h2 className="nav-title">HENA</h2>

      <div className="nav-buttons">
        <button onClick={handleProfileClick} title="Profile">
          <FaUserCircle size={20} />
        </button>
        <button onClick={handleLogout} title="Logout">
          <FaSignOutAlt size={20} />
        </button>
      </div>

      {popupNotif && (
        <div className="notification-popup">
          <strong>{popupNotif.type}:</strong> {popupNotif.content}
        </div>
      )}
    </nav>
  );
}
