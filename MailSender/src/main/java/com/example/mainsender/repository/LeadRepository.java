package com.example.mainsender.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.mainsender.entity.Lead;

@Repository
public interface LeadRepository extends MongoRepository<Lead, String> {
	Optional<Lead> findByEmailAndPhoneNumber(String email, String phoneNumber);
	boolean existsById(String id);
}