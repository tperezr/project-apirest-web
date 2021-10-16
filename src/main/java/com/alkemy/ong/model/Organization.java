package com.alkemy.ong.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organizations")
@SQLDelete(sql = "UPDATE organizations SET deleted = true WHERE id=?")
@Where(clause = " deleted = false ")
public class Organization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message="Name can not be empty")
	@Column(name = "name", nullable=false)
	private String name;
	
	@NotBlank(message="Image can not be empty")
	@Column(name = "image", nullable=false)
	private String image;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone")
	private int phone;
	
	@NotBlank(message="Email can not be empty")
	@Column(name = "email", nullable = false)
	private String email;
	
	@NotBlank(message="Welcome text can not be empty")
	@Column(name = "welcomeText", nullable=false)
	private String welcomeText;
	
	@Column(name = "aboutUs")
	private String aboutUs;
	
	@Column(name = "deleted", nullable=false)
	private boolean deleted;
	
	@Column(name="createdat")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name="updatedat")
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(name="urlFacebook")
	private String urlFacebook;

	@Column(name="urlLinkedin")
	private String urlLinkedin;

	@Column(name="urlInstragram")
	private String urlInstragram;

}
