package com.example.crm;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/** Runs against a real MongoDB (a service container in CI). */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ChatApiTests {
	@Autowired
	MockMvc mvc;
	@LocalServerPort
	int port;

	static String room() {
		return "room-" + UUID.randomUUID().toString().substring(0, 8);
	}

	@Test
	void createGetAndDuplicateRoom() throws Exception {
		String id = room();
		mvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
				.content("{\"roomId\":\"%s\",\"createdBy\":\"Razi\"}".formatted(id)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.roomId").value(id))
				.andExpect(jsonPath("$.createdBy").value("Razi"));
		mvc.perform(get("/api/v1/rooms/" + id)).andExpect(status().isOk());
		mvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
				.content("{\"roomId\":\"%s\",\"createdBy\":\"Asha\"}".formatted(id)))
				.andExpect(status().isConflict());
	}

	@Test
	void validationAndNotFound() throws Exception {
		mvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
				.content("{\"roomId\":\"a b\",\"createdBy\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fields.roomId").exists())
				.andExpect(jsonPath("$.fields.createdBy").exists());
		mvc.perform(get("/api/v1/rooms/does-not-exist-123")).andExpect(status().isNotFound());
	}

	@Test
	@SuppressWarnings("unchecked")
	void liveChatOverStompStoresHistoryAndTracksPresence() throws Exception {
		String id = room();
		mvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
				.content("{\"roomId\":\"%s\",\"createdBy\":\"Razi\"}".formatted(id))).andExpect(status().isCreated());

		var client = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
		client.setMessageConverter(new MappingJackson2MessageConverter());
		StompSession session = client.connectAsync("http://localhost:" + port + "/chat", new StompSessionHandlerAdapter() {
		}).get(10, TimeUnit.SECONDS);

		BlockingQueue<Map<String, Object>> messages = new LinkedBlockingQueue<>();
		BlockingQueue<Map<String, Object>> presence = new LinkedBlockingQueue<>();
		session.subscribe("/topic/rooms/" + id, frame(messages));
		session.subscribe("/topic/rooms/" + id + "/presence", frame(presence));
		Thread.sleep(300); // let the subscriptions register

		session.send("/app/rooms/" + id + "/join", Map.of("sender", "Razi"));
		assertEquals("JOIN", messages.poll(5, TimeUnit.SECONDS).get("type"));
		assertEquals(List.of("Razi"), presence.poll(5, TimeUnit.SECONDS).get("online"));

		session.send("/app/rooms/" + id + "/send", Map.of("sender", "Razi", "content", "Hello room"));
		Map<String, Object> chat = messages.poll(5, TimeUnit.SECONDS);
		assertEquals("Hello room", chat.get("content"));
		assertEquals("CHAT", chat.get("type"));
		assertNotNull(chat.get("timestamp"));

		mvc.perform(get("/api/v1/rooms/" + id + "/messages"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[1].content").value("Hello room"));
		mvc.perform(get("/api/v1/rooms/" + id)).andExpect(jsonPath("$.online[0]").value("Razi"));

		session.disconnect();
		client.stop();
	}

	private static StompFrameHandler frame(BlockingQueue<Map<String, Object>> sink) {
		return new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return Map.class;
			}

			@Override
			@SuppressWarnings("unchecked")
			public void handleFrame(StompHeaders headers, Object payload) {
				sink.add((Map<String, Object>) payload);
			}
		};
	}
}
