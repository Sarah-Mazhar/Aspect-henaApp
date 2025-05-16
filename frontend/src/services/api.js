import axios from "axios";

const API_BASE = "http://localhost:8080/api";

const staticCreds = {
  USER: { username: "user", password: "userpass" },
  HOST: { username: "host", password: "hostpass" },
  ADMIN: { username: "admin", password: "adminpass" },
};



export const login = async ({ username, password, role }) => {
  const staticUser = staticCreds[role];
  const authHeader = btoa(`${staticUser.username}:${staticUser.password}`);
  const config = {
    headers: {
      Authorization: `Basic ${authHeader}`,
    },
  };
  const response = await axios.post(`${API_BASE}/user/login`, { username, password, role }, config);
  return response.data;
};

export const createEvent = async ({ eventData, role = "ADMIN", adminId }) => {
  const staticUser = staticCreds[role];
  const authHeader = btoa(`${staticUser.username}:${staticUser.password}`);
  const config = {
    headers: {
      Authorization: `Basic ${authHeader}`,
    },
  };

  const response = await axios.post(
    `${API_BASE}/event/create/${adminId}`,
    eventData,
    config
  );
  return response.data;
};

export const createUser = async ({ formData, adminId }) => {
  const staticUser = staticCreds["ADMIN"];
  const authHeader = btoa(`${staticUser.username}:${staticUser.password}`);

  const config = {
    headers: {
      Authorization: `Basic ${authHeader}`,
    },
  };

  const response = await axios.post(
    `${API_BASE}/admin/createUser/${adminId}`,
    formData,
    config
  );

  return response.data;
};

export const getHostEvents = async (hostId) => {
  const staticUser = staticCreds["HOST"]; // or dynamic if needed
  const authHeader = btoa(`${staticUser.username}:${staticUser.password}`);
  
  const config = {
    headers: {
      Authorization: `Basic ${authHeader}`,
    },
  };

  const response = await axios.get(
    `${API_BASE}/event/host/${hostId}`,
    config
  );
  return response.data;
};

export const deleteEvent = async ({ eventId, hostId, role = "HOST" }) => {
  const staticUser = staticCreds[role];
  const authHeader = btoa(`${staticUser.username}:${staticUser.password}`);

  const config = {
    headers: {
      Authorization: `Basic ${authHeader}`,
    },
  };

  await axios.delete(`${API_BASE}/event/delete/${hostId}/${eventId}`, config);
};

export const getUpcomingEvents = async () => {
  const staticUser = staticCreds["USER"];
  const authHeader = btoa(`${staticUser.username}:${staticUser.password}`);

  const response = await axios.get("http://localhost:8080/api/event/upcoming", {
    headers: {
      Authorization: `Basic ${authHeader}`,
    },
  });

  return response.data;
};



export const rsvpToEvent = async ({ userId, eventId }) => {
  const staticUser = staticCreds["USER"];
  const authHeader = btoa(`${staticUser.username}:${staticUser.password}`);
  const config = {
    headers: {
      Authorization: `Basic ${authHeader}`,
    },
  };
  const response = await axios.post(`${API_BASE}/user/rsvp/${userId}/${eventId}`, {}, config);
  return response.data;
};

