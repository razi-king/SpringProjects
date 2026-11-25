// Set VITE_API_URL at build time (your Render URL); falls back to local Spring Boot.
export const API = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '')

export class ApiError extends Error {
  constructor(status, message, fields) {
    super(message)
    this.status = status
    this.fields = fields || {}
  }
}

export async function api(path, { method = 'GET', body } = {}) {
  let res
  try {
    res = await fetch(API + path, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError(0, "Can't reach the chat server. It may be waking up; try again in a minute.")
  }
  const data = await res.json().catch(() => null)
  if (!res.ok) throw new ApiError(res.status, data?.message || `Request failed (${res.status})`, data?.fields)
  return data
}

export const createRoom = (roomId, createdBy) => api('/api/v1/rooms', { method: 'POST', body: { roomId, createdBy } })
export const getRoom = (roomId) => api(`/api/v1/rooms/${encodeURIComponent(roomId)}`)
export const getHistory = (roomId, before) =>
  api(`/api/v1/rooms/${encodeURIComponent(roomId)}/messages?size=30${before ? `&before=${encodeURIComponent(before)}` : ''}`)
export const wake = () => fetch(`${API}/healthz`).catch(() => {})
