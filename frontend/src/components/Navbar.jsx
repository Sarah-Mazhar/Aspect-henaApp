import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { FaUserCircle, FaSignOutAlt } from "react-icons/fa";
import "./Navbar.css";

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [role, setRole] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userRole = localStorage.getItem("role");
    setIsLoggedIn(!!token);
    setRole(userRole);
  }, [location]);

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
      <div className="nav-logo gradient-text">HENA</div>

      {isLanding && (
        <div className="nav-right">
          <button onClick={() => navigate("/login")}>Login</button>
        </div>
      )}

      {!isLanding && !isLoginOrSignup && isLoggedIn && (
        <div className="nav-icons">
          <FaUserCircle className="nav-icon profile-icon" onClick={goToProfile} />
          <FaSignOutAlt className="nav-icon logout-icon" onClick={logout} />
        </div>
      )}
    </nav>
  );
}
