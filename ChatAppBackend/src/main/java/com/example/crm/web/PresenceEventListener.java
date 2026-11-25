package com.example.crm.web;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.crm.message.ChatMessage;
import com.example.crm.presence.PresenceService;

import lombok.RequiredArgsConstructor;

/** When a socket closes (tab closed, network lost, Leave clicked), announce the leave. */
@Component
@RequiredArgsConstructor
public class PresenceEventListener {
	private final PresenceService presence;
	private final ChatService chat;
	private final ChatSocketController socket;

	@EventListener
	public void onDisconnect(SessionDisconnectEvent event) {
		presence.leave(event.getSessionId()).ifPresent(m -> {
			if (!presence.online(m.roomId()).contains(m.name())) {
				socket.broadcast(chat.post(m.roomId(), m.name(), m.name() + " left", ChatMessage.Type.LEAVE));
			}
			socket.broadcastPresence(m.roomId());
		});
	}
}
