package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	
	@Schema(description = "email of the user", example = "admin01@email.com")
    private String email;
	
	@Schema(description = "password of the user", example = "secret")
    private String password;
	
}
