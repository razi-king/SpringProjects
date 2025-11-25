package com.example.chatApplication.dto;

import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sender;
	private String content;
	private LocalDateTime timeStamp;
	 @ManyToOne
	    @JoinColumn(name = "room_id")
	    private Room room;
	public Message(String sender, String content) {
		super();
		this.sender = sender;
		this.content = content;
		this.timeStamp=LocalDateTime.now();
	}
	
	
}
