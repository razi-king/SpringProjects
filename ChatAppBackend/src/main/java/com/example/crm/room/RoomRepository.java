package com.example.crm.room;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
	Optional<Room> findByRoomId(String roomId);

	boolean existsByRoomId(String roomId);
}
