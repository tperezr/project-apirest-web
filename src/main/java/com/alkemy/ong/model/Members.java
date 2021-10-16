package com.alkemy.ong.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "members")
@SQLDelete(sql = "UPDATE members SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    private String name;

    @Nullable
    private String facebookUrl;
    
    @Nullable
    private String instagramUrl;

    @Nullable
    private String linkedinUrl;

    @NotNull(message = "Image is required")
    private String image;
    
    @Nullable
    private String description;

    @NotNull
    private boolean deleted = Boolean.FALSE;

    @CreationTimestamp
    private Timestamp regdate;

    @UpdateTimestamp
    private Timestamp updatedate;
}
