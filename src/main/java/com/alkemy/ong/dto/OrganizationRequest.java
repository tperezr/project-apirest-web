package com.alkemy.ong.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class OrganizationRequest {
	
	private String name;
	private MultipartFile image;
	private String email;
	private String welcomeText;
	private Integer phone;
	private String address;
	private String aboutUs;
	private String urlFacebook;
	private String urlLinkedin;
	private String urlInstagram;
	
	public boolean hasName() {
    	return this.name != null && !this.name.isEmpty();
    }
	
	public boolean hasImage() {
    	return this.image != null && !this.image.isEmpty();
    }
	
	public boolean hasEmail() {
    	return this.email != null && !this.email.isEmpty();
    }
	
	public boolean hasWelcomeText() {
    	return this.welcomeText != null && !this.welcomeText.isEmpty();
    }
	
	public boolean hasPhone() {
    	return this.phone != null;
    }
	
	public boolean hasAddress() {
    	return this.address != null && !this.address.isEmpty();
    }
	
	public boolean hasAboutUs() {
    	return this.aboutUs != null && !this.aboutUs.isEmpty();
    }
	
	public boolean hasUrlFacebook() {
    	return this.urlFacebook != null && !this.urlFacebook.isEmpty();
    }
	
	public boolean hasUrlInstagram() {
    	return this.urlInstagram != null && !this.urlInstagram.isEmpty();
    }
	
	public boolean hasUrlLinkedin() {
    	return this.urlLinkedin != null && !this.urlLinkedin.isEmpty();
    }
	
	public boolean hasNoInfo() {
		return !hasInfo();
	}

	public boolean hasInfo() {
		return hasName() || hasImage() || hasEmail() ||
			   hasWelcomeText() || hasPhone() || hasAddress() || hasAboutUs() ||
			   hasUrlFacebook() || hasUrlInstagram() || hasUrlLinkedin();
	}
	
}
