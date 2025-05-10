package com.example.chatApplication.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String roomId;
	@ElementCollection
	private List<Message> message = new ArrayList<>();

}
