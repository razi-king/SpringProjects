package com.example.chatApplication.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.chatApplication.dto.Room;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {

	Room findRoomByRoomId(String roomId);
}
