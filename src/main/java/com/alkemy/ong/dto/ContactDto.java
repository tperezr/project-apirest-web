package com.alkemy.ong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    @NotBlank(message = "Name can not be empty.")
    private String name;
    private String phone;
    @NotBlank(message = "Email can not be empty.")
    @Email(message = "Not valid email.")
    private String email;
    private String message;
}
