package com.example.practice.controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.practice.dto.LeadDto;
import com.example.practice.entity.Lead;
import com.example.practice.response.LeadResponse;
import com.example.practice.service.EmailService;
import com.example.practice.service.LeadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {
	private final LeadService leadService;
	private final EmailService emailService;
	
	@PostMapping
	public ResponseEntity<LeadResponse> createLead(@Valid @ModelAttribute LeadDto lead,BindingResult result){
		
		if(result.hasErrors()) {
			String errors = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
			return ResponseEntity.badRequest().body(new LeadResponse(false,errors,null));
		}
			emailService.sendEmail("raziinsane@gmail.com","New Lead Submitted", "New Lead Submitted:\n\nName: " + lead.getName() + 
		            "\nEmail: " + lead.getEmail() + 
		            "\nPhone: " + lead.getPhoneNumber());
			emailService.sendEmail(lead.getEmail(), "Thankyou For Your Form Submission", "Dear " + lead.getName() + ",\n\nThank you for contacting us. " +
		            "We have received your details and will get back to you soon.\n\nRegards,\nTeam");
			Lead createlead = leadService.saveLead(lead);
			System.out.println(createlead);
			return ResponseEntity.ok(new LeadResponse(true, "submission saved", lead));
	}
}
