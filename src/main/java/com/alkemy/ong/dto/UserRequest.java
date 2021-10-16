package com.alkemy.ong.dto;

import com.alkemy.ong.model.Role;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private MultipartFile photo;

}
