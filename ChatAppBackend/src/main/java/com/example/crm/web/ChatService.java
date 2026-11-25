package com.example.crm.web;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.crm.message.ChatMessage;
import com.example.crm.message.MessageRepository;
import com.example.crm.presence.PresenceService;
import com.example.crm.room.Room;
import com.example.crm.room.RoomRepository;
import com.example.crm.web.Dtos.CreateRoomRequest;
import com.example.crm.web.Dtos.RoomResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final RoomRepository rooms;
	private final MessageRepository messages;
	private final PresenceService presence;

	public RoomResponse create(CreateRoomRequest req) {
		String id = req.roomId().trim();
		if (rooms.existsByRoomId(id)) {
			throw new ApiException(409, "room_exists", "A room with that ID already exists. Join it instead.");
		}
		Room r = rooms.save(new Room(id, req.createdBy().trim()));
		return toResponse(r);
	}

	public RoomResponse get(String roomId) {
		return toResponse(room(roomId));
	}

	/** Cursor pagination: up to `size` messages older than `before`, returned oldest → newest. */
	public List<ChatMessage> history(String roomId, Instant before, int size) {
		room(roomId);
		int capped = Math.max(1, Math.min(size, 100));
		List<ChatMessage> page = new ArrayList<>(messages.findByRoomIdAndTimestampBeforeOrderByTimestampDesc(
				roomId, before != null ? before : Instant.now().plusSeconds(1), PageRequest.of(0, capped)));
		Collections.reverse(page);
		return page;
	}

	public ChatMessage post(String roomId, String sender, String content, ChatMessage.Type type) {
		room(roomId);
		return messages.save(ChatMessage.of(roomId, sender.trim(), content.trim(), type));
	}

	private Room room(String roomId) {
		return rooms.findByRoomId(roomId)
				.orElseThrow(() -> new ApiException(404, "room_not_found", "No room with ID \"" + roomId + "\". Check the ID or create it."));
	}

	private RoomResponse toResponse(Room r) {
		return new RoomResponse(r.getRoomId(), r.getCreatedBy(), r.getCreatedAt(), presence.online(r.getRoomId()));
	}
}
