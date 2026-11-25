import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router'
import { Toaster } from 'react-hot-toast'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter basename={import.meta.env.BASE_URL}>
      <Toaster position="top-center" toastOptions={{ style: { background: '#1e1b3a', color: '#f1f5f9' } }} />
      <App />
    </BrowserRouter>
  </StrictMode>,
)
