import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { login } from "../../services/api";
import "./Login.css";

export default function Login() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    role: "",
  });

  const navigate = useNavigate();

  // Clear auth info when visiting login page
  useEffect(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("userId");
    localStorage.removeItem("adminId");
    localStorage.removeItem("hostId");
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.role) {
      alert("Please select a role.");
      return;
    }

    try {
      const result = await login(form);
      localStorage.setItem("token", result.token);
      localStorage.setItem("role", result.role);
      localStorage.setItem("userId", result.userId);

      if (result.role === "ADMIN") localStorage.setItem("adminId", result.userId);
      if (result.role === "HOST") localStorage.setItem("hostId", result.userId);

      navigate(`/${result.role.toLowerCase()}-dashboard`);
    } catch (error) {
      alert("Login failed: " + (error.response?.data || error.message));
    }
  };

  return (
    <motion.div
      className="login-page"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.6 }}
    >
      <nav className="navbar">
        <div className="nav-logo gradient-text">HENA</div>
      </nav>

      <div className="login-body">
        <motion.div
          className="login-image"
          initial={{ opacity: 0, x: -50 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: 100 }}
          transition={{ duration: 0.6 }}
        />

        <motion.div
          className="login-content"
          initial={{ opacity: 0, x: 50 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: -100 }}
          transition={{ duration: 0.6 }}
        >
          <div className="login-container">
            <h2 className="login-title login-purple">Log in</h2>

            <form className="login-form" onSubmit={handleSubmit}>
              <input
                name="username"
                placeholder="USERNAME"
                value={form.username}
                onChange={handleChange}
                required
              />
              <input
                name="password"
                type="password"
                placeholder="PASSWORD"
                value={form.password}
                onChange={handleChange}
                required
              />
              <select
                name="role"
                value={form.role}
                onChange={handleChange}
                required
              >
                <option value="">Select Role</option>
                <option value="USER">User</option>
                <option value="HOST">Host</option>
                <option value="ADMIN">Admin</option>
              </select>
              <button type="submit">Log In</button>
            </form>

            <p className="login-small-link">
              Don't have an account?{" "}
              <span className="link" onClick={() => navigate("/signup")}>
                Sign Up
              </span>
            </p>
          </div>
        </motion.div>
      </div>
    </motion.div>
  );
}
