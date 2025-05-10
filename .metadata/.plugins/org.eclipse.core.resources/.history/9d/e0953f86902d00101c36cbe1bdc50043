package com.example.chatApplication.dto;

import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
	private String sender;
	private String content;
	private LocalDateTime timeStamp;
	public Message(String sender, String content) {
		super();
		this.sender = sender;
		this.content = content;
		this.timeStamp=LocalDateTime.now();
	}
	
	
}
