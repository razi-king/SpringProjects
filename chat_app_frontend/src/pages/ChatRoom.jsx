import { useEffect, useLayoutEffect, useRef, useState } from 'react'
import { Navigate, useNavigate, useParams } from 'react-router'
import toast from 'react-hot-toast'
import Avatar from '../components/Avatar'
import useChatRoom from '../lib/useChatRoom'
import { session } from '../lib/session'

const time = (iso) => new Date(iso).toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' })
const day = (iso) => {
  const d = new Date(iso)
  const today = new Date()
  const yest = new Date(Date.now() - 864e5)
  if (d.toDateString() === today.toDateString()) return 'Today'
  if (d.toDateString() === yest.toDateString()) return 'Yesterday'
  return d.toLocaleDateString([], { day: 'numeric', month: 'long', year: 'numeric' })
}

const statusText = { connecting: 'Connecting…', online: 'Connected', reconnecting: 'Reconnecting…' }
const statusDot = { connecting: 'bg-amber-400', online: 'bg-emerald-400', reconnecting: 'bg-amber-400' }

function Messages({ messages, me }) {
  let lastDay = ''
  return messages.map((m, i) => {
    const d = day(m.timestamp)
    const sep = d !== lastDay
    lastDay = d
    const prev = messages[i - 1]
    const grouped = !sep && prev?.type === 'CHAT' && m.type === 'CHAT' && prev.sender === m.sender &&
      new Date(m.timestamp) - new Date(prev.timestamp) < 5 * 60 * 1000
    return (
      <div key={m.id || i}>
        {sep && (
          <div className="my-5 flex items-center gap-3 text-xs text-slate-500">
            <span className="h-px flex-1 bg-white/10" />{d}<span className="h-px flex-1 bg-white/10" />
          </div>
        )}
        {m.type !== 'CHAT' ? (
          <p className="my-2 text-center text-xs text-slate-500">{m.content} · {time(m.timestamp)}</p>
        ) : (
          <div className={`flex gap-3 ${m.sender === me ? 'flex-row-reverse' : ''} ${grouped ? 'mt-1' : 'mt-4'}`}>
            {grouped ? <span className="w-9 shrink-0" /> : <Avatar name={m.sender} />}
            <div className={`flex max-w-[75%] flex-col ${m.sender === me ? 'items-end' : 'items-start'}`}>
              {!grouped && (
                <p className="mb-1 text-xs text-slate-400">
                  <span className="font-semibold text-slate-200">{m.sender === me ? 'You' : m.sender}</span> · {time(m.timestamp)}
                </p>
              )}
              <p className={`whitespace-pre-wrap break-words rounded-2xl px-4 py-2 text-[15px] leading-relaxed ${m.sender === me ? 'rounded-tr-md bg-violet-500 text-white' : 'rounded-tl-md bg-white/[0.07] text-slate-100'}`}>
                {m.content}
              </p>
            </div>
          </div>
        )}
      </div>
    )
  })
}

export default function ChatRoom() {
  const { roomId } = useParams()
  const s = session.get()
  if (!s || s.roomId !== roomId) return <Navigate to={`/?room=${encodeURIComponent(roomId)}`} replace />
  return <Room roomId={roomId} name={s.name} />
}

function Room({ roomId, name }) {
  const navigate = useNavigate()
  const { messages, online, typing, status, hasMore, error, send, notifyTyping, loadOlder } = useChatRoom(roomId, name)
  const [text, setText] = useState('')
  const [showPeople, setShowPeople] = useState(false)
  const list = useRef(null)
  const stick = useRef(true)

  // Keep scrolled to the newest message unless the reader scrolled up.
  useLayoutEffect(() => {
    if (stick.current && list.current) list.current.scrollTop = list.current.scrollHeight
  }, [messages, typing])

  useEffect(() => { if (error) toast.error(error) }, [error])

  function onScroll() {
    const el = list.current
    stick.current = el.scrollHeight - el.scrollTop - el.clientHeight < 80
  }

  async function older() {
    const el = list.current
    const before = el.scrollHeight
    stick.current = false
    await loadOlder()
    requestAnimationFrame(() => { el.scrollTop = el.scrollHeight - before })
  }

  function submit(e) {
    e.preventDefault()
    const content = text.trim()
    if (!content) return
    if (send(content)) {
      setText('')
      stick.current = true
      notifyTyping(false)
    } else toast.error('Not connected yet. Your message was not sent.')
  }

  function onKey(e) {
    if (e.key === 'Enter' && !e.shiftKey) submit(e)
  }

  function leave() {
    session.clear()
    navigate('/')
  }

  async function invite() {
    const url = `${location.origin}${import.meta.env.BASE_URL}?room=${encodeURIComponent(roomId)}`
    try {
      await navigator.clipboard.writeText(url)
      toast.success('Invite link copied')
    } catch {
      toast(url)
    }
  }

  const typingText = typing.length === 1 ? `${typing[0]} is typing…` : typing.length > 1 ? `${typing.slice(0, 2).join(' and ')} are typing…` : ''

  return (
    <div className="flex h-[100dvh] bg-[#0b0a1a] text-slate-100">
      <div className="flex min-w-0 flex-1 flex-col">
        <header className="flex items-center justify-between gap-3 border-b border-white/10 px-4 py-3 sm:px-6">
          <div className="min-w-0">
            <h1 className="truncate text-lg font-semibold"># {roomId}</h1>
            <p className="flex items-center gap-1.5 whitespace-nowrap text-xs text-slate-400">
              <span className={`h-2 w-2 rounded-full ${statusDot[status]}`} aria-hidden="true" />
              <span>{statusText[status]}</span><span aria-hidden="true">·</span><span>{online.length} online</span>
            </p>
          </div>
          <div className="flex shrink-0 gap-1.5 sm:gap-2">
            <button onClick={() => setShowPeople((v) => !v)} className="rounded-lg border border-white/10 px-2.5 py-1.5 text-sm text-slate-300 hover:bg-white/5 lg:hidden" aria-expanded={showPeople}>People</button>
            <button onClick={invite} className="rounded-lg border border-white/10 px-2.5 py-1.5 text-sm text-slate-300 hover:bg-white/5 sm:px-3"><span className="sm:hidden">Invite</span><span className="hidden sm:inline">Copy invite</span></button>
            <button onClick={leave} className="rounded-lg bg-white/10 px-2.5 py-1.5 text-sm font-medium hover:bg-white/15 sm:px-3">Leave</button>
          </div>
        </header>

        <div ref={list} onScroll={onScroll} className="flex-1 overflow-y-auto px-4 py-4 sm:px-6" aria-live="polite">
          {hasMore && messages.length > 0 && (
            <div className="text-center">
              <button onClick={older} className="rounded-full border border-white/10 px-4 py-1.5 text-sm text-slate-400 hover:text-white">Load older messages</button>
            </div>
          )}
          {messages.length === 0 && !error && (
            <div className="grid h-full place-items-center text-center text-slate-500">
              <div>
                <p className="text-slate-300">No messages yet</p>
                <p className="mt-1 text-sm">Say hi, or copy the invite link and share it.</p>
              </div>
            </div>
          )}
          <Messages messages={messages} me={name} />
        </div>

        <form onSubmit={submit} className="border-t border-white/10 px-4 py-3 sm:px-6">
          <p className="h-5 text-xs text-slate-400" aria-live="polite">{typingText}</p>
          <div className="flex items-end gap-2">
            <label htmlFor="msg" className="sr-only">Message</label>
            <textarea
              id="msg"
              rows={1}
              value={text}
              maxLength={1000}
              onChange={(e) => { setText(e.target.value); notifyTyping(e.target.value.length > 0) }}
              onKeyDown={onKey}
              placeholder={`Message #${roomId}`}
              className="max-h-40 flex-1 resize-none rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-white placeholder:text-slate-500 outline-none focus:border-violet-400"
            />
            <button disabled={!text.trim() || status !== 'online'} className="rounded-xl bg-violet-500 px-5 py-3 font-semibold text-white hover:bg-violet-400 disabled:opacity-40">Send</button>
          </div>
          <p className="mt-1 text-right text-xs text-slate-500">Enter to send · Shift+Enter for a new line</p>
        </form>
      </div>

      <aside className={`${showPeople ? 'fixed inset-y-0 right-0 z-20 shadow-2xl' : 'hidden'} w-64 shrink-0 border-l border-white/10 bg-[#0e0d22] p-5 lg:static lg:block`}>
        <h2 className="mb-3 text-sm font-semibold text-slate-300">Online · {online.length}</h2>
        <ul className="space-y-2.5">
          {online.map((p) => (
            <li key={p} className="flex items-center gap-3">
              <span className="relative">
                <Avatar name={p} size={30} />
                <span className="absolute -bottom-0.5 -right-0.5 h-2.5 w-2.5 rounded-full border-2 border-[#0e0d22] bg-emerald-400" />
              </span>
              <span className="truncate text-sm">{p}{p === name ? ' (you)' : ''}</span>
            </li>
          ))}
        </ul>
      </aside>
    </div>
  )
}
