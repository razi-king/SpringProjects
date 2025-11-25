package com.example.mainsender.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponse {
	@Schema(
			description = "Status of the API call",
			type = "boolean",
			example = "true"
			)
	private Boolean success;

	@Schema(
			description = "Any object or list of objects returned on successful API call",
			example = "{...}"
			)
	private Object data;
	@Schema(
			description = "API response message",
			type = "string",
			example = "Operation successful"
			)
	private String message;
}
