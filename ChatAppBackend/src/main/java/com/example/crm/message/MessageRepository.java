package com.example.crm.message;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<ChatMessage, String> {
	/** Newest first; the service flips it so the UI gets oldest → newest. */
	List<ChatMessage> findByRoomIdAndTimestampBeforeOrderByTimestampDesc(String roomId, Instant before, Pageable page);
}
