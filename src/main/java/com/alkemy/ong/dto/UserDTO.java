package com.alkemy.ong.dto;

import com.alkemy.ong.model.Role;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private Role roleId;
}
