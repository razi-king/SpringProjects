package com.example.crm.room;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document("rooms")
public class Room {
	@Id
	private String id;

	@Indexed(unique = true)
	private String roomId;

	private String createdBy;
	private Instant createdAt = Instant.now();

	public Room(String roomId, String createdBy) {
		this.roomId = roomId;
		this.createdBy = createdBy;
	}
}
