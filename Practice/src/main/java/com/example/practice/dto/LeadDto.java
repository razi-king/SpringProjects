package com.example.practice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LeadDto {
	@NotBlank(message="Name Is Required")
	@Pattern(regexp="^[a-zA-Z\\s]+$",message="Name Can Only Contain Letter And Spaces")
	private String name;
	
	@NotBlank(message="Email Is Required")
	@Email(message="Invalid Email Format")
	private String email;
	
	@NotBlank(message="PhoneNumber Is Required")
	@Pattern(regexp="^\\d{10}$",message="Phone Number Must Be Exactly Of 10 Digits")
	private String phoneNumber;
}
