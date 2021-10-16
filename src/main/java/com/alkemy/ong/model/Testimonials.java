package com.alkemy.ong.model;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "testimonials")
@SQLDelete(sql = "UPDATE testimonials SET deleted=true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class Testimonials {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotBlank(message = "Name can not be empty")
    private String name;
    private String image;
    @NotBlank(message = "Content can not be empty")
    private String content;
    private boolean deleted;

    @CreationTimestamp
    private Date created_at;

    @UpdateTimestamp
    private Date updated_at;

	public Testimonials(String name, String content, String image) {
		super();
		this.name = name;
		this.image = image;
		this.content = content;
	}
    
    

}
