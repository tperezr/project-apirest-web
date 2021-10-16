package com.alkemy.ong.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "slides")
public class Slide {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotBlank(message = "Image url can not be empty")
    @Column(nullable = false)
    private String imageUrl;

    @NotBlank(message = "Text can not be empty")
    @Column(nullable = false)
    private String text;

    @NotNull(message = "Order can not be empty") @Min(value = 1,message = "Order must be >= 1")
    @Column(nullable = false,unique = true)
    private Integer slideOrder;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    @NotNull
    private Organization organization;

	public Slide(String imageUrl,String text,Integer slideOrder,Organization organization) {
		super();
		this.imageUrl = imageUrl;
		this.text = text;
		this.slideOrder = slideOrder;
		this.organization = organization;
	}
    
    
}
