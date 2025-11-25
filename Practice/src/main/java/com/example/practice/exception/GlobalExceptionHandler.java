package com.example.practice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.practice.response.LeadResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<LeadResponse> handleDuplicateException(DuplicateResourceException ex){
		LeadResponse leadResponse = new LeadResponse(false, ex.getMessage(), null);
		return new ResponseEntity<>(leadResponse,HttpStatus.CONFLICT);	
	}
}
