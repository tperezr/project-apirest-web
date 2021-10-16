package com.alkemy.ong.service;

import com.alkemy.ong.dto.LoginRequest;
import com.alkemy.ong.dto.RegistrationRequest;
import com.alkemy.ong.dto.UserDTO;

import org.springframework.http.ResponseEntity;

public interface IAuthService{
    ResponseEntity<Object> register(RegistrationRequest request);
    ResponseEntity<Object> login(LoginRequest loginRequest);
    UserDTO findByEmail(String email);
    LoginRequest createLoginRequest(RegistrationRequest reqModel);
}
