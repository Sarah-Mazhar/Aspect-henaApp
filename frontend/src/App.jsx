import { Routes, Route, Navigate } from "react-router-dom";
import Register from "./features/auth/Register";
import Login from "./features/auth/Login";
import UserDashboard from "./features/dashboard/UserDashboard";
import HostDashboard from "./features/dashboard/HostDashboard";
import AdminDashboard from "./features/dashboard/AdminDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import CreateEvent from "./features/events/CreateEvent";
import EditEvent from "./features/events/EditEvent";

function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/signup" element={<Register />} />
            <Route path="/login" element={<Login />} />

            <Route
                path="/user-dashboard"
                element={
                    <ProtectedRoute roleRequired="USER">
                        <UserDashboard />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/host-dashboard"
                element={
                    <ProtectedRoute roleRequired="HOST">
                        <HostDashboard />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/admin-dashboard"
                element={
                    <ProtectedRoute roleRequired="ADMIN">
                        <AdminDashboard />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/create-event"
                element={
                    <ProtectedRoute roleRequired="HOST">
                        <CreateEvent />
                    </ProtectedRoute>
                }
            />

            <Route path="/edit-event" element={<EditEvent />} />

        </Routes>
    );
}

export default App;
