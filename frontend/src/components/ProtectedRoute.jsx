// src/components/ProtectedRoute.jsx
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ roleRequired, children }) {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (!token || role !== roleRequired) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
