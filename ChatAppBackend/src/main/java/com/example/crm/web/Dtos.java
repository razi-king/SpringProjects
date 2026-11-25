package com.example.crm.web;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class Dtos {
	private Dtos() {
	}

	public static final String ROOM_ID = "^[A-Za-z0-9_-]{3,30}$";
	public static final String ROOM_ID_MSG = "Room ID must be 3 to 30 letters, numbers, - or _";

	public record CreateRoomRequest(
			@NotBlank(message = "Room ID is required") @Pattern(regexp = ROOM_ID, message = ROOM_ID_MSG) String roomId,
			@NotBlank(message = "Your name is required") @Size(max = 30, message = "Name must be at most 30 characters") String createdBy) {
	}

	public record RoomResponse(String roomId, String createdBy, Instant createdAt, List<String> online) {
	}

	/** Payloads sent over STOMP. */
	public record SendMessage(
			@NotBlank @Size(max = 30) String sender,
			@NotBlank @Size(max = 1000, message = "Message is too long") String content) {
	}

	public record JoinRequest(@NotBlank @Size(max = 30) String sender) {
	}

	public record Typing(@NotBlank @Size(max = 30) String sender, boolean typing) {
	}

	public record Presence(String roomId, List<String> online) {
	}
}
