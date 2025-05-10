import { httpClient } from "../config/AxiosHelper"

export const createRoom = async (roomDetail)=>{
    const response=await httpClient.post(`/api/v1/room`,roomDetail,{
        headers: {
            "Content-Type": "text/plain",
        },
    });
    return response.data;
}
export const joinChatAPI = async  (roomId)=>{
    const response=await httpClient.get(`/api/v1/room/${roomId}`)
    return response.data;
}