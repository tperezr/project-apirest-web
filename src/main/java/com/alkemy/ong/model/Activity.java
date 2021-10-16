package com.alkemy.ong.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "activities")
@SQLDelete(sql = "UPDATE activities SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name can not be empty.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Content can not be empty.")
    @Column(nullable = false)
    private String content;

    @NotNull(message = "Image url can not be empty.")
    @Column(nullable = false)
    private String image;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    private Boolean deleted = Boolean.FALSE;
}
