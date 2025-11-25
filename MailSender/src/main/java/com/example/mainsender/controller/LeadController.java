package com.example.mainsender.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mainsender.dto.LeadRequest;
import com.example.mainsender.entity.Lead;
import com.example.mainsender.response.LeadResponse;
import com.example.mainsender.service.LeadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LeadController {
	@Autowired
	private final LeadService leadService;
	@PostMapping ResponseEntity<LeadResponse> saveLead(@ModelAttribute @Valid LeadRequest lead) {
		 try {
			leadService.saveLead(lead);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return ResponseEntity.ok(new LeadResponse());
	}
	@GetMapping("/getAll")
	public List<Lead> getAllLead(){
		return leadService.getAllLead();
	}
	
}
