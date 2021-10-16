package com.alkemy.ong.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE news SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class News {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Name may not be empty")
	private String name;
	
	@NotBlank(message = "Content can not be empty")
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@NotBlank(message = "Image can not be empty")
	private String image;
	
	@ManyToOne
	@JoinColumn(name ="category_id")
	private Category categoryId;
	
	@CreationTimestamp
	private LocalDateTime createdAT;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@Column
	private boolean deleted = Boolean.FALSE;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy="news")
    private List<Comment> coments;

	public News(String name, String content, String image, Category categoryId) {
		super();
		this.name = name;
		this.content = content;
		this.image = image;
		this.categoryId = categoryId;
	}
	
	
	
}
