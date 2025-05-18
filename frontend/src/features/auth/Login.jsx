import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../../services/api";
import "./Login.css";

export default function Login() {
  const [form, setForm] = useState({
    username: "",
    password: "",
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const result = await login({ ...form, role: "USER" });
      localStorage.setItem("token", result.token);
      localStorage.setItem("role", result.role);
      localStorage.setItem("userId", result.userId);
      navigate(`/${result.role.toLowerCase()}-dashboard`);
    } catch (error) {
      alert("Login failed: " + (error.response?.data || error.message));
    }
  };

  return (
    <div className="login-page">
      <nav className="navbar">
        <div className="nav-logo gradient-text">HENA</div>
      </nav>

      <div className="login-body">
        <div className="login-image" />
        <div className="login-content">
          <div className="login-container">
            <h2 className="login-title login-purple">Log in</h2>
            <p className="login-subtitle">
              Welcome Back !
            </p>

            <form className="login-form" onSubmit={handleSubmit}>
              <input
                name="username"
                placeholder="USERNAME"
                onChange={handleChange}
                required
              />
              <input
                name="password"
                type="password"
                placeholder="PASSWORD"
                onChange={handleChange}
                required
              />
              <button type="submit">Log In</button>
            </form>

            <p className="login-small-link">
              Don't have an account?{" "}
              <span className="link" onClick={() => navigate("/signup")}>
                Sign Up
              </span>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
