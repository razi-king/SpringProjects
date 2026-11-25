// Who you are and which room you're in survive a page refresh (per tab).
const KEY = 'chat.session'
const RECENT = 'chat.recent'

export const session = {
  get: () => {
    try { return JSON.parse(sessionStorage.getItem(KEY) || 'null') } catch { return null }
  },
  set: (s) => {
    sessionStorage.setItem(KEY, JSON.stringify(s))
    localStorage.setItem('chat.name', s.name)
    const recent = [s.roomId, ...recentRooms().filter((r) => r !== s.roomId)].slice(0, 5)
    localStorage.setItem(RECENT, JSON.stringify(recent))
  },
  clear: () => sessionStorage.removeItem(KEY),
}

export const lastName = () => localStorage.getItem('chat.name') || ''
export function recentRooms() {
  try { return JSON.parse(localStorage.getItem(RECENT) || '[]') } catch { return [] }
}
