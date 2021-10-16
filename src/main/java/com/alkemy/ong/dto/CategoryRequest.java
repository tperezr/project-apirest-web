package com.alkemy.ong.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Pattern;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
	@Schema(description = "Name of the Category")
	private String name;

	@Schema(description = "Description of the Category")
    private String description;

	@Schema(description = "Image of the Category")
    //@Pattern(regexp = "^https?://.+\\.(?:png|jpe?g)$", message = "invalid url image")
    private MultipartFile image;

	public boolean hasName() {
		return this.name != null;
	}
	
	public boolean hasDescription() {
		return this.description != null;
	}
	
	public boolean hasImage() {
		return this.image != null;
	}

}
