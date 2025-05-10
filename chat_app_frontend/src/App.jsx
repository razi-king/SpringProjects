import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import toast from 'react-hot-toast'
import JoinCreateChat from './Components/JoinCreateChat'

function App() {
  const [count, setCount] = useState(0)

  return (
    // <div>
    //   <h1>This Is Main Page:</h1>
    //   <button onClick={()=>{toast.success("This Is Toast Message")}}>
    //     Click Me
    //   </button>
    // </div>
    <>
      <JoinCreateChat/>
    </>
  )
}

export default App
