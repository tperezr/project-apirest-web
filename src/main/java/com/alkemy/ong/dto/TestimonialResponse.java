package com.alkemy.ong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialResponse {
	private String name;
	private String content;
	private String image;
}
