package com.alkemy.ong.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ActivityRequest {
	
    private String name;
    private String content;
    private MultipartFile image;
    
    public boolean hasName() {
    	return this.name != null;
    }
    
    public boolean hasContent() {
    	return this.content != null;
    }
    
    public boolean hasImage() {
    	return this.image != null;
    }
    

}
