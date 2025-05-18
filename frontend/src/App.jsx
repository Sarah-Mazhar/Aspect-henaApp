import { Routes, Route, Navigate } from "react-router-dom";
import Register from "./features/auth/Register";
import Login from "./features/auth/Login";
import UserDashboard from "./features/dashboard/UserDashboard";
import HostDashboard from "./features/dashboard/HostDashboard";
import AdminDashboard from "./features/dashboard/AdminDashboard";
import CreateEventPage from "./features/admin/CreateEventPage";
import AllEventsPage from "./features/admin/AllEventsPage";
import ProtectedRoute from "./components/ProtectedRoute";
import EditEventPage from "./features/admin/EditEventPage"; 
import CreateUserPage from "./features/admin/CreateUserPage";
import HostEventsPage from "./features/host/HostEventsPage";
import HostEditEventPage from "./features/host/HostEditEventPage";
import HostCreateEventPage from "./features/host/HostCreateEventPage";
import AdminProfilePage from "./features/admin/adminProfilePage"; 
import HostProfilePage from "./features/host/HostProfilePage";
import UserProfilePage from "./features/user/UserProfilePage"; 
import LandingPage from './features/home/LandingPage';
import { useLocation } from "react-router-dom";
import { AnimatePresence } from "framer-motion";


function App() {
  const location = useLocation();
  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<LandingPage />} />
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
      <Route
        path="/admin/edit-event/:id"
        element={
          <ProtectedRoute roleRequired={["ADMIN"]}>
            <EditEventPage />
          </ProtectedRoute>
        }
      />

    
      <Route
        path="/host/create-event"
        element={
          <ProtectedRoute roleRequired={["HOST"]}>
            <HostCreateEventPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/host/events/:hostId"
        element={
          <ProtectedRoute roleRequired={["HOST"]}>
            <HostEventsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/host/edit-event/:eventId"
        element={
          <ProtectedRoute roleRequired={["HOST"]}>
            <HostEditEventPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/profile/:userId"
        element={
          <ProtectedRoute roleRequired={["USER"]}>
            <UserProfilePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/profile/:adminId"
        element={
          <ProtectedRoute roleRequired={["ADMIN"]}>
            <AdminProfilePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/host/profile/:userId"
        element={
          <ProtectedRoute roleRequired={["HOST"]}>
            <HostProfilePage />
          </ProtectedRoute>
        }
      />


    </Routes>
    </AnimatePresence>
  );
}

export default App;
