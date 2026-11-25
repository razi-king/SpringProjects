package com.example.crm.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** One error shape for the REST API: {status, error, message, fields?}. */
@RestControllerAdvice
public class ErrorHandler {
	public record ApiError(int status, String error, String message, Map<String, String> fields) {
	}

	@ExceptionHandler(ApiException.class)
	ResponseEntity<ApiError> api(ApiException ex) {
		return ResponseEntity.status(ex.getStatus()).body(new ApiError(ex.getStatus(), ex.getCode(), ex.getMessage(), null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
		Map<String, String> fields = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(f -> fields.putIfAbsent(f.getField(), f.getDefaultMessage()));
		return ResponseEntity.badRequest().body(new ApiError(400, "validation_failed", "Please fix the highlighted fields.", fields));
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
	ResponseEntity<ApiError> unreadable() {
		return ResponseEntity.badRequest().body(new ApiError(400, "bad_request", "The request could not be read.", null));
	}
}
