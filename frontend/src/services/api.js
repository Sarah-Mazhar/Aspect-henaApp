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
