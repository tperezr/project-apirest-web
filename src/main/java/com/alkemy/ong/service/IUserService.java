package com.alkemy.ong.service;


import com.alkemy.ong.dto.UserRequest;
import com.alkemy.ong.model.*;
import com.alkemy.ong.dto.UserDTO;

import java.util.List;

public interface IUserService {
    List<UserDTO> findAllUsers();
    String softDeleteUser(long id);
    User updateUser(Long id, UserRequest user);

}
