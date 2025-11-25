package com.example.practice.service;

import com.example.practice.dto.LeadDto;
import com.example.practice.entity.Lead;

public interface LeadService {
	Lead saveLead(LeadDto leadDto);
}
