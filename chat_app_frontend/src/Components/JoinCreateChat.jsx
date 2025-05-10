import React, { useState } from 'react'
import chatIcon from '../assets/chat.png'
import toast from 'react-hot-toast'
import { createRoom as createRoomAPI, joinChatAPI } from '../Services/RoomService';
import useChatContext from '../Context/ChatContext';
import { useNavigate } from 'react-router';
export default function JoinCreateChat() {
  const [detail,setDetail] = useState({
    roomId:"",
    userName:"",
  });
  const { roomId, setRoomId, currentUser, setCurrentUser, connected, setConnected } = useChatContext();
  const navigate =useNavigate();
  function handleFormInputChange(event){
    setDetail({...detail,[event.target.name]:event.target.value,});
  }
  function validateForm(){
    if(detail.roomId===""|| detail.userName===""){
      toast.error("Invalid Input !!")
      return false;
    }
    return true;
  }
  async function joinChat(){
    if(validateForm()){
      //Join Chat
      
      try{
        const response =await joinChatAPI(detail.roomId);
        toast.success('Joined Successfully');
        setCurrentUser(detail.userName);
        setRoomId(response.roomId);
        setConnected(true);
        navigate('/chat');
      }catch(error){
        if(error.status==400){
          toast.error(error.response.data);
        }
        else{
          toast.error("Error In Joining Room");
        }
        console.log(error);
      }
    }
  }
  async function createRoom(){
    if(validateForm()){
      // Create Room 
      // Call Api To Create Room On Backend
      try{
        const response=await createRoomAPI(detail.roomId)
        console.log(response)
        toast.success("Room Created Successfully !!");
        //Join The Room
        setCurrentUser(detail.userName);
        setRoomId(response.roomId);
        setConnected(true);
        navigate('/chat');
      }catch(error){
        console.log(error);
        if(error.status==400){
          toast.error("Room Already Exists !!")
        }
        else{
          toast("Error Occured In Creating Room")
        }
      }
    }
  }
  return (
    <div className='flex justify-center items-center min-h-screen w-screen bg-gray-800'>
        <div className='flex flex-col gap-5 p-8 border max-w-md shadow rounded-lg bg-gray-900 text-white'>
          <div>
            <img src={chatIcon} alt="Chat Icon" className='w-20 mx-auto' loading='lazy'/>
          </div>
          <h1 className='text-center font-bold text-2xl'>Join Room / Create Room</h1>
          <div className=''>
            <label htmlFor="name" className='block font-medium mb-2 '>Your Name</label>
            <input
              onChange={handleFormInputChange}
              value={detail.userName}
              name='userName'
              className='w-full px-4 py-2 rounded focus:outline-none focus:ring-2 focus:ring-blue-500  text-xl' id='name' type='text' placeholder='Enter Your Name'/>
          </div>
          <div className=''>
            <label htmlFor="name" className='block font-medium mb-2 '>Room ID / New Room ID</label>
            <input
             name='roomId'
             onChange={handleFormInputChange}
             value={detail.roomId}
             className='w-full px-4 py-2 rounded focus:outline-none focus:ring-2 focus:ring-blue-500  text-xl' id='name' type='text' placeholder='Enter Your Name'/>
          </div>
           <div className='flex justify-between'>
            <button onClick={()=>joinChat()} className='bg-blue-500 px-3 hover:bg-blue-800 py-2 transition-all duration-300 rounded-lg text-white font-semibold '>Join Room</button>
            <button onClick={()=>createRoom()} className='bg-green-500 px-3 hover:bg-green-800 py-2 transition-all duration-300 rounded-lg text-white font-semibold '>Create Room</button>
           </div>
        </div>
    </div>
  )
}
