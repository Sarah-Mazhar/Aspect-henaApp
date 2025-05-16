import { Routes, Route, Navigate } from "react-router-dom";
import Register from "./features/auth/Register";
import Login from "./features/auth/Login";
import UserDashboard from "./features/dashboard/UserDashboard";
import HostDashboard from "./features/dashboard/HostDashboard";
import AdminDashboard from "./features/dashboard/AdminDashboard";
import CreateEventPage from "./features/admin/CreateEventPage";
import AllEventsPage from "./features/admin/AllEventsPage";
import ProtectedRoute from "./components/ProtectedRoute";
import CreateUserPage from "./features/admin/CreateUserPage";

function App() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/signup" element={<Register />} />
      <Route path="/login" element={<Login />} />

      {/* Protected Routes */}
      <Route
        path="/user-dashboard"
        element={
          <ProtectedRoute roleRequired={["USER"]}>
            <UserDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/host-dashboard"
        element={
          <ProtectedRoute roleRequired={["HOST"]}>
            <HostDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin-dashboard"
        element={
          <ProtectedRoute roleRequired={["ADMIN"]}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/create-event"
        element={
          <ProtectedRoute roleRequired={["ADMIN"]}>
            <CreateEventPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/events"
        element={
          <ProtectedRoute roleRequired={["ADMIN"]}>
            <AllEventsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/create-user"
        element={
          <ProtectedRoute roleRequired={["ADMIN"]}>
            <CreateUserPage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;