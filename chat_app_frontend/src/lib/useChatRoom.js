import { useCallback, useEffect, useRef, useState } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { API, getHistory } from './api'

/**
 * Everything live about one room: STOMP connection (with auto-reconnect),
 * message history, online users and who is typing.
 * status: 'connecting' | 'online' | 'reconnecting'
 */
export default function useChatRoom(roomId, name) {
  const [messages, setMessages] = useState([])
  const [online, setOnline] = useState([])
  const [typing, setTyping] = useState([])
  const [status, setStatus] = useState('connecting')
  const [hasMore, setHasMore] = useState(true)
  const [error, setError] = useState('')
  const client = useRef(null)
  const typingTimers = useRef({})

  // Initial history
  useEffect(() => {
    let alive = true
    getHistory(roomId)
      .then((page) => {
        if (!alive) return
        setMessages(page)
        setHasMore(page.length === 30)
      })
      .catch((e) => alive && setError(e.message))
    return () => { alive = false }
  }, [roomId])

  // Live connection
  useEffect(() => {
    const c = new Client({
      webSocketFactory: () => new SockJS(`${API}/chat`),
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        setStatus('online')
        c.subscribe(`/topic/rooms/${roomId}`, (f) => {
          const m = JSON.parse(f.body)
          setMessages((prev) => (prev.some((p) => p.id === m.id) ? prev : [...prev, m]))
          if (m.type === 'CHAT') clearTyping(m.sender)
        })
        c.subscribe(`/topic/rooms/${roomId}/presence`, (f) => setOnline(JSON.parse(f.body).online))
        c.subscribe(`/topic/rooms/${roomId}/typing`, (f) => {
          const t = JSON.parse(f.body)
          if (t.sender === name) return
          if (t.typing) {
            setTyping((prev) => (prev.includes(t.sender) ? prev : [...prev, t.sender]))
            clearTimeout(typingTimers.current[t.sender])
            typingTimers.current[t.sender] = setTimeout(() => clearTyping(t.sender), 4000)
          } else clearTyping(t.sender)
        })
        c.publish({ destination: `/app/rooms/${roomId}/join`, body: JSON.stringify({ sender: name }) })
      },
      onWebSocketClose: () => setStatus((s) => (s === 'online' ? 'reconnecting' : s)),
      onStompError: (f) => setError(f.headers.message || 'Connection error'),
    })
    client.current = c
    c.activate()
    return () => { c.deactivate() }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [roomId, name])

  function clearTyping(who) {
    clearTimeout(typingTimers.current[who])
    setTyping((prev) => prev.filter((p) => p !== who))
  }

  const send = useCallback((content) => {
    const c = client.current
    if (!c?.connected) return false
    c.publish({ destination: `/app/rooms/${roomId}/send`, body: JSON.stringify({ sender: name, content }) })
    return true
  }, [roomId, name])

  const lastTypingSent = useRef(0)
  const notifyTyping = useCallback((isTyping) => {
    const c = client.current
    if (!c?.connected) return
    const now = Date.now()
    if (isTyping && now - lastTypingSent.current < 2000) return // throttle
    lastTypingSent.current = isTyping ? now : 0
    c.publish({ destination: `/app/rooms/${roomId}/typing`, body: JSON.stringify({ sender: name, typing: isTyping }) })
  }, [roomId, name])

  const loadOlder = useCallback(async () => {
    const oldest = messages[0]?.timestamp
    if (!oldest) return
    const page = await getHistory(roomId, oldest)
    setMessages((prev) => [...page, ...prev])
    setHasMore(page.length === 30)
  }, [messages, roomId])

  return { messages, online, typing, status, hasMore, error, send, notifyTyping, loadOlder }
}
