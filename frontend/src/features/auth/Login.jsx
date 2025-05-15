import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Register.css";
import { login } from "../../services/api";

export default function Login() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    role: "USER",
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const result = await login(form);
      console.log("Login result:", result);

      localStorage.setItem("token", result.token);
      localStorage.setItem("role", result.role);
      localStorage.setItem("userId", result.userId);

      if (result.role === "ADMIN") {
        localStorage.setItem("adminId", result.userId);
      } else {
        localStorage.removeItem("adminId");
      }

      if (result.role === "HOST") {
        localStorage.setItem("hostId", result.userId);
      } else {
        localStorage.removeItem("hostId");
      }

      navigate(`/${result.role.toLowerCase()}-dashboard`);
    } catch (error) {
      console.error("Login error:", error);
      if (error.response && error.response.data) {
        alert("Login failed: " + error.response.data);
      } else {
        alert("Login failed: " + error.message);
      }
    }
  };

  return (
    <div className="signup-container">
      <h2>Login</h2>
      <form className="signup-form" onSubmit={handleSubmit}>
        <input
          name="username"
          placeholder="Username"
          onChange={handleChange}
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          onChange={handleChange}
          required
        />
        <select name="role" value={form.role} onChange={handleChange}>
          <option value="USER">User</option>
          <option value="HOST">Host</option>
          <option value="ADMIN">Admin</option>
        </select>
        <button type="submit">Login</button>
      </form>

      <div className="text-link">
        Don’t have an account?
        <span className="link" onClick={() => navigate("/signup")}>
          Sign up
        </span>
      </div>
    </div>
  );
}
