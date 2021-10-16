package com.alkemy.ong.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentRequest {

	@NotNull(message = "News id can not be null")
	private Long newsId;
	
	@NotEmpty(message = "Comment body can not be empty")
	private String body;
	
}
