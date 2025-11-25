package com.example.mainsender.service;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mainsender.dto.LeadRequest;
import com.example.mainsender.entity.Lead;
import com.example.mainsender.repository.LeadRepository;

import jakarta.validation.Valid;

@Service
public class LeadService {
	@Autowired
	private LeadRepository leadRepository;
	Lead lead;
	public Lead saveLead(@Valid LeadRequest req) throws Exception {
		if(leadRepository.findByEmailAndPhoneNumber(req.getEmail(), req.getPhoneNumber()).isPresent()) {
			System.out.println("Duplicate Name And Email");
			throw new Exception("Duplicate Name And Email");
		}
		
			String last4 = req.getPhoneNumber().substring(6);
			String uniqueId;
			do {
				int random4 =1000 + (int)(Math.random()*9000);
				uniqueId = random4 + last4;
				System.out.println(uniqueId);
				System.out.println("-------------------------------");
			}while(leadRepository.existsById(uniqueId));
			 lead = new Lead(uniqueId, req.getName().trim(), req.getEmail().toLowerCase(), req.getPhoneNumber(),
					req.getUtmSource(), req.getUtmMedium(), req.getUtmCampaign(), new Date());
		
		return leadRepository.save(lead);
	}
	public ArrayList<Lead> getAllLead() {
		return (ArrayList<Lead>) leadRepository.findAll();
	}

}
