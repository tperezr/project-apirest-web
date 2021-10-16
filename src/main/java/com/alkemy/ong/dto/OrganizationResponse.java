package com.alkemy.ong.dto;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {

	private String name;
	private String image;
	private int phone;
	private String address;
	private String urlFacebook;
	private String urlLinkedin;
	private String urlInstagram;
}
