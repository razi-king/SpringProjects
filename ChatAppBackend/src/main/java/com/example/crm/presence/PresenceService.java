package com.example.crm.presence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * Who is online in which room, keyed by WebSocket session. In memory, so it resets on
 * restart (fine: clients rejoin when they reconnect). Use Redis for multiple instances.
 */
@Service
public class PresenceService {
	public record Member(String roomId, String name) {
	}

	private final Map<String, Member> sessions = new ConcurrentHashMap<>();

	/** Returns true if this name was not already online in the room (so a JOIN is worth announcing). */
	public boolean join(String sessionId, String roomId, String name) {
		boolean isNew = !online(roomId).contains(name);
		sessions.put(sessionId, new Member(roomId, name));
		return isNew;
	}

	public Optional<Member> leave(String sessionId) {
		return Optional.ofNullable(sessions.remove(sessionId));
	}

	public List<String> online(String roomId) {
		return sessions.values().stream()
				.filter(m -> m.roomId().equals(roomId))
				.map(Member::name)
				.distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();
	}
}
