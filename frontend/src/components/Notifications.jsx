import { useEffect, useState } from "react";
import axios from "axios";
import "./Notifications.css";

export default function Notifications({ userId }) {
  const [notifications, setNotifications] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);

  useEffect(() => {
    if (!userId) return;

    axios.get(`/api/notifications/user/${userId}`)
      .then((res) => setNotifications(res.data.reverse()))
      .catch((err) => console.error("❌ Failed to fetch notifications:", err));
  }, [userId]);

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="notification-bell">
      <button onClick={() => setShowDropdown(!showDropdown)}>
        🔔 {unreadCount > 0 ? <span className="notif-count">{unreadCount}</span> : ""}
      </button>
      {showDropdown && (
        <div className="dropdown">
          {notifications.length === 0 ? (
            <p className="empty">No notifications</p>
          ) : (
            <ul>
              {notifications.map((n) => (
                <li key={n.id}>
                  <strong>{n.type}</strong>: {n.content}
                  <br />
                  <small>{new Date(n.timestamp).toLocaleString()}</small>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}
