import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router'
import { createRoom, getRoom, wake } from '../lib/api'
import { lastName, recentRooms, session } from '../lib/session'

const ROOM_RE = /^[A-Za-z0-9_-]{3,30}$/

export default function Lobby() {
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const invited = params.get('room') || ''
  const [mode, setMode] = useState('join')
  const [name, setName] = useState(lastName())
  const [roomId, setRoomId] = useState(invited)
  const [errors, setErrors] = useState({})
  const [banner, setBanner] = useState('')
  const [busy, setBusy] = useState(false)
  const recent = recentRooms()

  useEffect(() => { wake() }, [])

  function validate() {
    const e = {}
    if (!name.trim()) e.name = 'Enter your name.'
    else if (name.trim().length > 30) e.name = 'At most 30 characters.'
    if (!ROOM_RE.test(roomId.trim())) e.roomId = 'Use 3 to 30 letters, numbers, - or _.'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  async function submit(ev) {
    ev.preventDefault()
    setBanner('')
    if (!validate()) return
    setBusy(true)
    try {
      const id = roomId.trim()
      if (mode === 'create') await createRoom(id, name.trim())
      else await getRoom(id)
      session.set({ name: name.trim(), roomId: id })
      navigate(`/room/${encodeURIComponent(id)}`)
    } catch (err) {
      setErrors(err.fields?.roomId ? { roomId: err.fields.roomId } : {})
      setBanner(err.message)
    } finally {
      setBusy(false)
    }
  }

  const input = 'w-full rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-white placeholder:text-slate-500 outline-none focus:border-violet-400 focus:ring-2 focus:ring-violet-400/30'

  return (
    <main className="grid min-h-screen place-items-center bg-[#0b0a1a] px-4 py-12 text-slate-100">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 grid h-14 w-14 place-items-center rounded-2xl bg-violet-500 text-2xl">💬</div>
          <h1 className="text-3xl font-semibold tracking-tight">Rooms</h1>
          <p className="mt-2 text-slate-400">Real-time chat over WebSockets. Pick a name and a room.</p>
        </div>

        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-6 shadow-2xl">
          <div role="tablist" className="mb-6 grid grid-cols-2 rounded-xl bg-white/5 p-1 text-sm font-medium">
            {['join', 'create'].map((m) => (
              <button
                key={m}
                role="tab"
                aria-selected={mode === m}
                onClick={() => { setMode(m); setBanner(''); setErrors({}) }}
                className={`rounded-lg py-2 transition ${mode === m ? 'bg-violet-500 text-white' : 'text-slate-400 hover:text-white'}`}
              >
                {m === 'join' ? 'Join a room' : 'Create a room'}
              </button>
            ))}
          </div>

          {banner && <p role="alert" className="mb-4 rounded-lg border border-red-400/30 bg-red-500/10 p-3 text-sm text-red-200">{banner}</p>}

          <form onSubmit={submit} noValidate className="space-y-4">
            <div className="space-y-1.5">
              <label htmlFor="name" className="text-sm font-medium text-slate-300">Your name</label>
              <input id="name" className={input} value={name} onChange={(e) => setName(e.target.value)} maxLength={30} autoComplete="nickname" aria-invalid={!!errors.name} />
              {errors.name && <p className="text-sm text-red-300">{errors.name}</p>}
            </div>
            <div className="space-y-1.5">
              <label htmlFor="room" className="text-sm font-medium text-slate-300">Room ID</label>
              <input id="room" className={input} value={roomId} onChange={(e) => setRoomId(e.target.value)} maxLength={30} placeholder={mode === 'create' ? 'e.g. weekend-plans' : 'Ask a friend for the ID'} aria-invalid={!!errors.roomId} />
              {errors.roomId && <p className="text-sm text-red-300">{errors.roomId}</p>}
            </div>
            <button disabled={busy} className="w-full rounded-xl bg-violet-500 py-3 font-semibold text-white transition hover:bg-violet-400 disabled:opacity-60">
              {busy ? 'Connecting…' : mode === 'join' ? 'Join room' : 'Create and join'}
            </button>
          </form>

          {recent.length > 0 && (
            <div className="mt-6 border-t border-white/10 pt-4">
              <p className="mb-2 text-sm text-slate-400">Recent rooms</p>
              <div className="flex flex-wrap gap-2">
                {recent.map((r) => (
                  <button key={r} onClick={() => { setRoomId(r); setMode('join') }} className="rounded-full border border-white/10 px-3 py-1 text-sm text-slate-300 hover:border-violet-400 hover:text-white">
                    {r}
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>
        <p className="mt-6 text-center text-xs text-slate-500">Spring Boot · STOMP over WebSocket · MongoDB · React</p>
      </div>
    </main>
  )
}
