import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaUserPlus } from "react-icons/fa";
import "./Register.css";

function Register() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    role: "USER",
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError(false);
    setLoading(true);

    try {
      const res = await fetch("/api/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      if (!res.ok) {
        throw new Error(await res.text());
      }

      const data = await res.json();
      setMessage(`✅ Registered: ${data.username}`);
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      setError(true);
      setMessage(err.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="landing-wrapper">
      <nav className="navbar">
        <div className="nav-logo gradient-text">HENA</div>
      </nav>

      <div className="register-container">
        <FaUserPlus className="register-icon" />
        <h1 className="register-title gradient-text">Create Account</h1>
        <form className="signup-form" onSubmit={handleSubmit}>
          <input
            name="username"
            placeholder="Username"
            value={formData.username}
            onChange={handleChange}
            required
          />
          <input
            name="email"
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required
          />
          <select name="role" value={formData.role} onChange={handleChange}>
            <option value="USER">User</option>
            <option value="HOST">Host</option>
            <option value="ADMIN">Admin</option>
          </select>
          <button type="submit" disabled={loading}>
            {loading ? "Registering..." : "Register"}
          </button>
        </form>

        {message && (
          <div className={error ? "message error" : "message success"}>
            {message}
          </div>
        )}

        <div className="text-link">
          Already have an account?
          <span className="link" onClick={() => navigate("/login")}>
            Login
          </span>
        </div>
      </div>
    </div>
  );
}

export default Register;
