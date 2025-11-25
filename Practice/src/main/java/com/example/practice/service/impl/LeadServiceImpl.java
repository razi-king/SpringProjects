package com.example.practice.service.impl;

import org.springframework.stereotype.Service;

import com.example.practice.dto.LeadDto;
import com.example.practice.entity.Lead;
import com.example.practice.exception.DuplicateResourceException;
import com.example.practice.repository.LeadRepository;
import com.example.practice.service.LeadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService{
	private final LeadRepository leadRepository;

	@Override
	public Lead saveLead(LeadDto leadDto) {
		if(leadRepository.existsByEmailAndPhoneNumber(leadDto.getEmail(),leadDto.getPhoneNumber())) {
			throw new DuplicateResourceException("Duplicate Email And Contact");
		}
		Lead lead = new Lead();
		lead.setName(leadDto.getName());
		lead.setEmail(leadDto.getEmail());
		lead.setPhoneNumber(leadDto.getPhoneNumber());
		return leadRepository.save(lead);
	}
	
	
}
