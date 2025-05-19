import axios from "axios";

const API_BASE = "http://localhost:8080/api";

// Static credentials per role
const staticCreds = {
  USER: { username: "user", password: "userpass" },
  HOST: { username: "host", password: "hostpass" },
  ADMIN: { username: "admin", password: "adminpass" },
};

// Helper to generate Basic Auth header
const getAuthHeader = (role) => {
  const { username, password } = staticCreds[role];
  return `Basic ${btoa(`${username}:${password}`)}`;
};

/* ================================
   AUTHENTICATION
================================= */

export const login = async ({ username, password, role }) => {
  const config = {
    headers: {
      Authorization: getAuthHeader(role),
    },
  };
  const response = await axios.post(`${API_BASE}/user/login`, { username, password, role }, config);
  return response.data;
};

/* ================================
   USER MANAGEMENT (Admin-managed)
================================= */

export const createUser = async ({ formData, adminId }) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("ADMIN"),
    },
  };
  const response = await axios.post(`${API_BASE}/admin/createUser/${adminId}`, formData, config);
  return response.data;
};

/* ================================
   USER PROFILE (USER)
================================= */

export const getUserProfile = async (userId) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("USER"),
    },
  };
  const response = await axios.get(`${API_BASE}/user/${userId}`, config);
  return response.data;
};

export const updateUserById = async (userId, payload) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("USER"),
      "Content-Type": "application/json",
    },
  };
  const response = await axios.put(`${API_BASE}/user/update/${userId}`, payload, config);
  return response.data;
};

/* ================================
   HOST PROFILE
================================= */

export const getHostProfile = async (hostId) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("HOST"),
    },
  };
  const response = await axios.get(`${API_BASE}/user/${hostId}`, config);
  return response.data;
};

export const updateHostProfile = async (hostId, updatedData) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("HOST"),
      "Content-Type": "application/json",
    },
  };
  const response = await axios.put(`${API_BASE}/user/update/${hostId}`, updatedData, config);
  return response.data;
};

/* ================================
   ADMIN PROFILE
================================= */

export const getAdminProfile = async (adminId) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("ADMIN"),
    },
  };
  const response = await axios.get(`${API_BASE}/user/${adminId}`, config);
  return response.data;
};

export const updateAdminProfile = async (adminId, updatedData) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("ADMIN"),
      "Content-Type": "application/json",
    },
  };
  const response = await axios.put(`${API_BASE}/user/update/${adminId}`, updatedData, config);
  return response.data;
};

/* ================================
   EVENT OPERATIONS - CREATE
================================= */

export const createEventByHost = async ({ eventData, role = "HOST", adminId }) => {
  const config = {
    headers: {
      Authorization: getAuthHeader(role),
    },
  };
  const response = await axios.post(`${API_BASE}/event/create/${adminId}`, eventData, config);
  return response.data;
};

export const createEventByAdmin = async (adminId, eventData) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("ADMIN"),
    },
  };
  const response = await axios.post(`${API_BASE}/event/create/${adminId}`, eventData, config);
  return response.data;
};

/* ================================
   EVENT OPERATIONS - READ
================================= */

export const getUpcomingEvents = async () => {
  const config = {
    headers: {
      Authorization: getAuthHeader("USER"),
    },
  };
  const response = await axios.get(`${API_BASE}/event/upcoming`, config);
  return response.data;
};

export const getHostEvents = async (hostId) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("HOST"),
    },
  };
  const response = await axios.get(`${API_BASE}/event/host/${hostId}`, config);
  return response.data;
};

export const getAllEventsForAdmin = async () => {
  const config = {
    headers: {
      Authorization: getAuthHeader("ADMIN"),
    },
  };
  const response = await axios.get(`${API_BASE}/event/all`, config);
  return response.data;
};

export const getEventById = async (eventId) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("HOST"),
    },
  };
  const response = await axios.get(`${API_BASE}/event/${eventId}`, config);
  return response.data;
};

/* ================================
   EVENT OPERATIONS - UPDATE
================================= */

export const updateEventByHost = async (hostId, eventId, updatedData) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("HOST"),
    },
  };
  const response = await axios.put(`${API_BASE}/event/update/${hostId}/${eventId}`, updatedData, config);
  return response.data;
};

export const updateEventByAdmin = async ({ hostId, eventId, updatedData }) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("ADMIN"),
    },
  };
  const response = await axios.put(`${API_BASE}/event/update/${hostId}/${eventId}`, updatedData, config);
  return response.data;
};

/* ================================
   EVENT OPERATIONS - DELETE
================================= */

export const deleteEvent = async ({ eventId, hostId, role = "HOST" }) => {
  const config = {
    headers: {
      Authorization: getAuthHeader(role),
    },
  };
  await axios.delete(`${API_BASE}/event/delete/${hostId}/${eventId}`, config);
};

/* ================================
   RSVP OPERATIONS
================================= */

export const rsvpToEvent = async ({ userId, eventId }) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("USER"),
    },
  };
  const response = await axios.post(`${API_BASE}/user/rsvp/${userId}/${eventId}`, {}, config);
  return response.data;
};

export const cancelRSVP = async ({ userId, eventId }) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("USER"),
    },
  };
  const response = await axios.delete(`${API_BASE}/user/rsvp/${userId}/${eventId}`, config);
  return response.data;
};

/* ================================
   NOTIFICATIONS
================================= */

export const fetchUserNotifications = async (userId) => {
  const config = {
    headers: {
      Authorization: getAuthHeader("USER"),
    },
  };
  const response = await axios.get(`${API_BASE}/notifications/user/${userId}`, config);
  return response.data;
};
