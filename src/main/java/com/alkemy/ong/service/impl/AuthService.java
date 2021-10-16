package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.LoginRequest;
import com.alkemy.ong.dto.RegistrationRequest;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.service.IAuthService;
import com.alkemy.ong.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    private final UserServiceImpl userService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private final MessageSource messageSource;
    private JwtUtil jwtTokenUtil;
    private final AmazonClient amazonClient;

    @Override
    public ResponseEntity<Object> register(RegistrationRequest request) {
    	User newUser = new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword());
    	if (request.getPhoto() != null)
    		newUser.setPhoto(amazonClient.uploadFile(request.getPhoto()));
        String register = userService.signUpUser(newUser);
        String error = messageSource.getMessage("register.error", null, Locale.US);
        log.info("{}", register);
        if (register != error) {
            LoginRequest loginRequest = this.createLoginRequest(request);
            return this.login(loginRequest);
        } else {
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<Object> login(LoginRequest loginRequest)  {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var userReturn = findByEmail(loginRequest.getEmail());

            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            final String jwt = jwtTokenUtil.generateToken(userDetails);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("jwt",jwt);

            return new ResponseEntity<>(userReturn, responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            String loginErrorMsg = messageSource.getMessage("login.error", new Object[]{" Invalid"}, Locale.US);
            HashMap<String, Boolean> error = new HashMap();
            error.put("ok:", false);
            return new ResponseEntity<>(error + loginErrorMsg, HttpStatus.UNAUTHORIZED);
        }



    }

    @Override
    public UserDTO findByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        UserDTO userDTO = new UserDTO();
        if (user != null && user.isPresent()) {
            userDTO.setFirstName(user.get().getFirstName());
            userDTO.setLastName(user.get().getLastName());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setPhoto(user.get().getPhoto());
            userDTO.setRoleId(user.get().getRoleId());
        }
        return userDTO;
    }

    @Override
    public LoginRequest createLoginRequest(RegistrationRequest reqModel){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(reqModel.getEmail());
        loginRequest.setPassword(reqModel.getPassword());
        return loginRequest;
    }
}
