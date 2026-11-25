package com.example.crm.web;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.crm.message.ChatMessage;
import com.example.crm.web.Dtos.CreateRoomRequest;
import com.example.crm.web.Dtos.RoomResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoomController {
	private final ChatService chat;

	@PostMapping("/api/v1/rooms")
	@ResponseStatus(HttpStatus.CREATED)
	public RoomResponse create(@Valid @RequestBody CreateRoomRequest req) {
		return chat.create(req);
	}

	@GetMapping("/api/v1/rooms/{roomId}")
	public RoomResponse get(@PathVariable String roomId) {
		return chat.get(roomId);
	}

	/** Older history: pass the timestamp of the oldest message you have as `before`. */
	@GetMapping("/api/v1/rooms/{roomId}/messages")
	public List<ChatMessage> history(@PathVariable String roomId, @RequestParam(required = false) Instant before,
			@RequestParam(defaultValue = "30") int size) {
		return chat.history(roomId, before, size);
	}

	@GetMapping("/healthz")
	public Map<String, String> health() {
		return Map.of("status", "ok");
	}
}
