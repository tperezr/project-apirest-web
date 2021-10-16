package com.alkemy.ong.dto;

import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
	
    @NotNull
    @Schema(description = "name of the user", example = "Lionel")
    private final String firstName;
    
    @NotNull
    @Schema(description = "last name of the user", example = "Messi")
    private final String lastName;
    
    @NonNull
    @NotNull
    @Schema(description = "email of the user", example = "leomessi@gmail.com")
    private String email;
    
    @NotNull
    @Schema(description = "password of the user", example = "barcelona123")
    private final String password;
    
    @Schema(description = "photo of the user")
    private MultipartFile photo;
    
}
