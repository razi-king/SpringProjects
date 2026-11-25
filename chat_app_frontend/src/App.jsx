import { Route, Routes, Navigate } from 'react-router'
import Lobby from './pages/Lobby'
import ChatRoom from './pages/ChatRoom'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Lobby />} />
      <Route path="/room/:roomId" element={<ChatRoom />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
