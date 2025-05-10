import React from 'react'
import {Routes,Route} from 'react-router';
import App from '../App';
import ChatPage from '../Components/ChatPage';
export default function MyRoutes() {
  return (
    <Routes>
        <Route path='/' element={<App/>}/>
        <Route path='/chat' element={<ChatPage/>}/>
    </Routes>
  )
}
