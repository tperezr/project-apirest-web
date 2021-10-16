package com.alkemy.ong.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembersRequest {
	
	public String name;
	public String facebookUrl;
	public String instagramUrl;
	public String linkedinUrl;
	public MultipartFile image;
	public String description;

}
