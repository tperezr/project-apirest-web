package com.alkemy.ong.dto;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

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
public class SlideRequest {

	MultipartFile image;
	String text;
	int slideOrder;
	
	public boolean hasText() {
		return !text.isBlank();
	}
	
	public boolean hasSlideOrder() {
		return slideOrder > 0;
	}

	public boolean hasImage() {
		return image != null;
	}
}
