package com.example.mainsender.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "website_leads")
public class Lead {
	@Id
	private String id;
	private String name;
	private String email;
	private String phoneNumber;
	private String utmSource;
	private String utmMedium;
	private String utmCampaign;
	private Date createdAt;
}
