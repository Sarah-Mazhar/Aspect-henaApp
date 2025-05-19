import { useEffect, useState, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { FaUserCircle, FaSignOutAlt, FaArrowLeft } from "react-icons/fa";
import { fetchUserNotifications } from "../services/api";
import "./Navbar.css";

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [role, setRole] = useState(null);
  const [popupNotif, setPopupNotif] = useState(null);
  const lastSeenRef = useRef(Date.now());

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userRole = localStorage.getItem("role");

    setIsLoggedIn(!!token);
    setRole(userRole);
  }, [location]);

  useEffect(() => {
    if (!isLoggedIn) return;

    const interval = setInterval(fetchNewNotification, 1500);
    return () => clearInterval(interval);
  }, [isLoggedIn]);

  const fetchNewNotification = async () => {
    const userId = localStorage.getItem("userId");
    if (!userId) return;

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

  const goToProfile = () => {
    const userId = localStorage.getItem("userId");

    if (role === "USER") navigate(`/profile/${userId}`);
    else if (role === "HOST") navigate(`/host/profile/${userId}`);
    else if (role === "ADMIN") navigate(`/admin/profile/${userId}`);
  };

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const isLanding = location.pathname === "/";
  const isLoginOrSignup =
    location.pathname === "/login" || location.pathname === "/signup";

  return (
    <nav className="navbar">
      <div className="nav-left">
        {!isLanding && !isLoginOrSignup && isLoggedIn && (
          <FaArrowLeft
            className="nav-icon back-icon"
            onClick={() => navigate(-1)}
            title="Go Back"
          />
        )}
        <div className="nav-logo gradient-text">HENA</div>
      </div>

      {isLanding && (
        <div className="nav-right">
          <button onClick={() => navigate("/login")}>Login</button>
        </div>
      )}

      {!isLanding && !isLoginOrSignup && isLoggedIn && (
        <div className="nav-icons">
          <FaUserCircle
            className="nav-icon profile-icon"
            onClick={goToProfile}
            title="Profile"
          />
          <FaSignOutAlt
            className="nav-icon logout-icon"
            onClick={logout}
            title="Logout"
          />
        </div>
      )}

      {popupNotif && (
        <div className="notification-popup">
          <strong>{popupNotif.type}:</strong> {popupNotif.content}
        </div>
      )}
    </nav>
  );
}
