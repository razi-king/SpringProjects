import axios from 'axios'
// Set VITE_API_URL at build time (e.g. your Render URL); falls back to local Spring Boot
export const baseURL = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '');
export const httpClient = axios.create(
    {
        baseURL:baseURL
    }
);