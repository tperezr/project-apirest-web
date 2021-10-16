package com.alkemy.ong.dto;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialRequest {
	

	@Schema(description = "Name of the testimonial", example = "Lorem ipsum dolor sit amet")
	private String name;
	
	@Schema(description = "Content of the testimonial", example = "Lorem ipsum dolor sit amet consectetur adipisicing elit. "
			+ "Officiis pariatur aperiam nobis, inventore aspernatur eum, non aliquam dicta quae dolorum laudantium dignissimos, "
			+ "doloribus voluptatum tempore incidunt reprehenderit veritatis ipsam. Odit?.")
	private String content;

	private MultipartFile image;


}
