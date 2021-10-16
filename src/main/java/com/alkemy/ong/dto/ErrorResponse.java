package com.alkemy.ong.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ErrorResponse {

	private Date timestamp;
	private int statusCode;
	private String message;
	private String path;

}
