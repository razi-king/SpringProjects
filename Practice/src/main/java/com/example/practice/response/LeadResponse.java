package com.example.practice.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeadResponse {
	private boolean success;
	private String message;
	private Object data;
}
