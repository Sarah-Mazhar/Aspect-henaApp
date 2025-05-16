import axios from "axios";

const instance = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json"
    }
});

// Set Basic Auth instead of Bearer token
instance.interceptors.request.use((config) => {
    // You can optionally check if the user is logged in here
    const username = "host";
    const password = "hostpass";
    const basicAuth = "Basic " + btoa(`${username}:${password}`);

    config.headers = {
        ...config.headers,
        Authorization: basicAuth,
    };

    return config;
});

export default instance;
