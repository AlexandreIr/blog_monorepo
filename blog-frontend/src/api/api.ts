import axios from "axios";

export const api = axios.create({
  baseURL: "https://libertad-api.onrender.com",
});

api.interceptors.request.use((config) => {
  const isLoginRoute = config.url === "/api/auth/login";

  if (isLoginRoute) {
    return config;
  }

  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});