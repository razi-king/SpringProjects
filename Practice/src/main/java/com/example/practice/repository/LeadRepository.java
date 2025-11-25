package com.example.practice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.practice.entity.Lead;


public interface LeadRepository extends MongoRepository<Lead, String> {
	boolean existsByEmailAndPhoneNumber(String email,String phoneNumber);
}
