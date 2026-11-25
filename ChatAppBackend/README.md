# 💬 Rooms: real-time chat

Room-based chat with **Spring Boot**, **STOMP over WebSocket (SockJS)** and **MongoDB**, with a React frontend in [`../chat_app_frontend`](../chat_app_frontend).

**Live:** https://razi-king.github.io/SpringProjects/ (the backend is hosted on Render and can take about a minute to wake up)

## Features

- Create or join rooms; share an invite link
- Live messages broadcast to everyone in the room
- **Who's online** per room, updated as people join, leave or lose connection
- **Typing indicator** (throttled, never stored)
- JOIN / LEAVE system messages, day separators, grouped bubbles
- Message history with **cursor pagination** ("Load older messages")
- Auto-reconnect with a connection status indicator

## Design notes

- Messages live in their own `messages` collection with a `{roomId, timestamp}` index, rather than an ever-growing array inside the room document. That avoids MongoDB's 16 MB document limit and makes paging fast.
- Presence is tracked per WebSocket session and cleaned up on `SessionDisconnectEvent`. It's in memory, so with more than one instance it should move to Redis.
- The REST API returns one error shape: `{status, error, message, fields?}`.

## API

| Kind | Destination | Purpose |
|---|---|---|
| `POST` | `/api/v1/rooms` | Create a room `{roomId, createdBy}` → 201 / 409 |
| `GET` | `/api/v1/rooms/{roomId}` | Room details + who's online |
| `GET` | `/api/v1/rooms/{roomId}/messages?before=&size=` | History, oldest → newest |
| STOMP send | `/app/rooms/{roomId}/join` · `/send` · `/typing` | Join, send a message, typing on/off |
| STOMP subscribe | `/topic/rooms/{roomId}` · `/presence` · `/typing` | Messages, online users, typing |

## Run locally

```bash
docker run -d -p 27017:27017 mongo:7          # or a MongoDB Atlas URI in MONGODB_URI
cd ChatAppBackend && ./mvnw spring-boot:run   # http://localhost:8080
cd ../chat_app_frontend && npm install && npm run dev
```

Tests (`mvn test`) include a live STOMP-over-SockJS test: it joins a room, sends a message, and checks the broadcast, presence and stored history.
