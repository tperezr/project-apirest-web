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
public class MemberDto {

	private String name;

	private String facebookUrl;

	private String instagramUrl;

	private String linkedinUrl;

	private String image;

	private String description;


}
