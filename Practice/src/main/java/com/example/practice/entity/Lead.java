package com.example.practice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="website_leads")
public class Lead {
	@Id
	private String id;
	private String name;
	private String email;
	private String phoneNumber;
	
}
