import React, { useState, useRef, useEffect } from 'react';
import { MdAttachment, MdSend } from 'react-icons/md';
import useChatContext from '../Context/ChatContext';
import { useNavigate } from 'react-router';
import { baseURL } from '../config/AxiosHelper';
import toast from 'react-hot-toast';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default function ChatPage() {
    const { roomId, setRoomId, currentUser, setCurrentUser, connected, setConnected } = useChatContext();
    const [message, setMessage] = useState([
        {
            sender: "Razi",
            content: "How Are You"
        },
    ]);
    const [input, setInput] = useState("");
    const inputRef = useRef(null);
    const chatBoxRef = useRef(null);
    const [stompClient, setStompClient] = useState(null);
    const navigate = useNavigate();

    // Handle WebSocket connection
    useEffect(() => {
        const connectWebSocket = () => {
            const socket = new SockJS(`${baseURL}/chat`);
            const client = Stomp.over(socket);
            client.connect({}, () => {
                setStompClient(client);  // Store stompClient after connection
                toast.success("Connected");
                client.subscribe(`/topic/room/${roomId}`, (message) => {
                    const newMessage = JSON.parse(message.body);
                    setMessage((prev) => [...prev, newMessage]);
                });
            });
        };

        // Only try to connect if connected is true
        if (connected) {
            connectWebSocket();
        }
    }, [connected, roomId]); // Add `connected` to ensure WebSocket connection is attempted when connected is true

    // If not connected, redirect to home
    useEffect(() => {
        if (!connected) {
            navigate('/');
        }
    }, [connected, navigate]); // This ensures the navigate function only fires when connected state changes

    // Send message to server
    const sendMessage = async () => {
        if (stompClient && stompClient.connected && input.trim()) {
            const message = {
                sender: currentUser,
                content: input,
                roomId: roomId
            };
            stompClient.send(`/app/sendMessage/${roomId}`, {}, JSON.stringify(message));
            setInput(""); // Clear the input after sending the message
        } else {
            toast.error("Not connected or empty message!");
        }
    };

    return (
        <div className=''>
            {/* This Is A Header */}
            <header className='flex justify-around text-white fixed w-full py-5 shadow bg-gray-800 items-center'>
                {/* Room Name */}
                <div>
                    <h1 className='text-xl font-semibold'>Room:<span>Family</span></h1>
                </div>
                {/* User Name */}
                <div>
                    <h1 className='text-xl font-semibold'>User:<span>Razi</span></h1>
                </div>
                {/* Leave Button */}
                <div>
                    <button className='bg-red-500 hover:bg-red-700 px-3 py-2 rounded'>Leave Room</button>
                </div>
            </header>
            {/* Main Content */}
            <main ref={chatBoxRef} className='py-20 h-screen w-2/3 px-5 overflow-auto mx-auto bg-slate-600 text-white'>
                {message.map((m, i) => (
                    <div key={i} className=''>
                        <div className='my-2 bg-blue-600 rounded max-w-xs'>
                            <div className='flex gap-2 p-2'>
                                <img src={'https://avatar.iran.liara.run/public/18'} alt="Profile Picture" loading='lazy' className='h-10' />
                                <div className='flex flex-col gap-1'>
                                    <p className='text-sm font-bold'>{m.sender}</p>
                                    <p>{m.content}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </main>
            {/* Input Message Container */}
            <div className='fixed bottom-2 w-full h-16'>
                <div className='h-full w-full md:w-2/3 mx-auto bg-gray-800 rounded flex justify-around items-center text-white px-2'>
                    <input
                        ref={inputRef}
                        type='text'
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        placeholder='Type Your Message Here...'
                        className='bg-gray-700 px-5 focus:outline-none py-2 rounded w-2/3'
                    />
                    <div className='flex gap-2'>
                        <button className='bg-green-500 w-10 h-10 rounded flex justify-center items-center'>
                            <MdAttachment size={20} />
                        </button>
                        <button onClick={sendMessage} className='bg-green-500 w-10 h-10 rounded flex justify-center items-center'>
                            <MdSend size={20} />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
