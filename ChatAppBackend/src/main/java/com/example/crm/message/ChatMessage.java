package com.example.crm.message;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Messages live in their own collection (not inside the room document), so a busy
 * room can't hit MongoDB's 16 MB document limit and history can be paged with an index.
 */
@Data
@NoArgsConstructor
@Document("messages")
@CompoundIndex(name = "room_time", def = "{'roomId': 1, 'timestamp': -1}")
public class ChatMessage {
	public enum Type { CHAT, JOIN, LEAVE }

	@Id
	private String id;
	private String roomId;
	private String sender;
	private String content;
	private Type type = Type.CHAT;
	private Instant timestamp = Instant.now();

	public static ChatMessage of(String roomId, String sender, String content, Type type) {
		ChatMessage m = new ChatMessage();
		m.roomId = roomId;
		m.sender = sender;
		m.content = content;
		m.type = type;
		return m;
	}
}
