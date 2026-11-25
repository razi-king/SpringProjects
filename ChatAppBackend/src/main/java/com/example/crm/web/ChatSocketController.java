package com.example.crm.web;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.crm.message.ChatMessage;
import com.example.crm.presence.PresenceService;
import com.example.crm.web.Dtos.JoinRequest;
import com.example.crm.web.Dtos.Presence;
import com.example.crm.web.Dtos.SendMessage;
import com.example.crm.web.Dtos.Typing;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * STOMP endpoints (clients send to /app/..., subscribe to /topic/...):
 *   /app/rooms/{id}/join   → /topic/rooms/{id} (JOIN message) + /topic/rooms/{id}/presence
 *   /app/rooms/{id}/send   → /topic/rooms/{id}
 *   /app/rooms/{id}/typing → /topic/rooms/{id}/typing (not stored)
 */
@Controller
@RequiredArgsConstructor
public class ChatSocketController {
	private final ChatService chat;
	private final PresenceService presence;
	private final SimpMessagingTemplate broker;

	@MessageMapping("/rooms/{roomId}/join")
	public void join(@DestinationVariable String roomId, @Valid @Payload JoinRequest req, SimpMessageHeaderAccessor headers) {
		String name = req.sender().trim();
		boolean isNew = presence.join(headers.getSessionId(), roomId, name);
		if (isNew) {
			broadcast(chat.post(roomId, name, name + " joined", ChatMessage.Type.JOIN));
		}
		broadcastPresence(roomId);
	}

	@MessageMapping("/rooms/{roomId}/send")
	public void send(@DestinationVariable String roomId, @Valid @Payload SendMessage req) {
		broadcast(chat.post(roomId, req.sender(), req.content(), ChatMessage.Type.CHAT));
	}

	@MessageMapping("/rooms/{roomId}/typing")
	public void typing(@DestinationVariable String roomId, @Valid @Payload Typing req) {
		broker.convertAndSend("/topic/rooms/" + roomId + "/typing", req);
	}

	void broadcast(ChatMessage m) {
		broker.convertAndSend("/topic/rooms/" + m.getRoomId(), m);
	}

	void broadcastPresence(String roomId) {
		broker.convertAndSend("/topic/rooms/" + roomId + "/presence", new Presence(roomId, presence.online(roomId)));
	}
}
