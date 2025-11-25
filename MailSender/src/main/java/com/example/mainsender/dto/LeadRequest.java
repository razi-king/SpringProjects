package com.example.mainsender.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LeadRequest {
	@NotBlank(message="Name is required")
	@Pattern(regexp = "^[a-zA-Z\\s]+$,message=Name can only contain letter and spaces")
	private String name;
	@NotBlank(message="Email is required")
	@Email(message="Invalid email format")
	private String email;
	@NotBlank(message="Contact is required")
	@Pattern(regexp ="^\\d{10}$,message=Phone must be exactly 10 digits")
	private String phoneNumber;
	
	private String utmSource;
	private String utmMedium;
	private String utmCampaign;
}