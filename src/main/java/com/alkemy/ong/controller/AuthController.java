package com.alkemy.ong.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alkemy.ong.dto.LoginRequest;
import com.alkemy.ong.dto.RegistrationRequest;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.service.IAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Authentication")
@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
public class AuthController {

    @Autowired
    private final MessageSource messageSource;
    @Autowired
    private IAuthService authService;

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary="Creates a new User",description = "returns if the user was created or not")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "the user was created successfully"),
    		@ApiResponse(responseCode = "400", description = "the fields required are missing ")})
    public ResponseEntity<Object> register(@ModelAttribute RegistrationRequest registrationReqModel) {
        ResponseEntity<Object> responseEntity = authService.register(registrationReqModel);
        if(responseEntity.getStatusCode().equals(UNAUTHORIZED)){
            return ResponseEntity.status(UNAUTHORIZED).body(responseEntity);
        } else {
            return ResponseEntity.ok(responseEntity);
        }
    }

    @PostMapping(value = "/login")
    @Operation(summary = "Log in a user", description = "returns if the user can log in or not")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "the user was logged successfully"),
    		@ApiResponse(responseCode = "400", description = "the fields required are missing")
    })
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        ResponseEntity<?> responseEntity = authService.login(loginRequest);
        if(responseEntity.getStatusCode().equals(UNAUTHORIZED)){
            return ResponseEntity.status(UNAUTHORIZED).body(responseEntity);
        } else {
            return ResponseEntity.ok(responseEntity);
        }
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "returns user's info", description = "returns all the information of the user")
    @ApiResponse(responseCode = "200", description = "user's data")
    public ResponseEntity<?> getName(Authentication authentication, Principal principal) {
        try {
            UserDTO userDto =  authService.findByEmail(authentication.getName());
            return ResponseEntity.ok(userDto);
        }catch( NullPointerException e ){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(messageSource.getMessage("error.jwt.noUserAuth", new Object[] { "jwt"}, Locale.US));
        }
    }
}
