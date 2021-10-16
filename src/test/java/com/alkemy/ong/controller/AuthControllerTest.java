package com.alkemy.ong.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.alkemy.ong.dto.LoginRequest;
import com.alkemy.ong.dto.RegistrationRequest;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.service.IAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IAuthService authService;
    @Autowired
    private UserRepository userRepository;

    private RegistrationRequest registrationRequest;
    private final LoginRequest loginRequest = new LoginRequest();
    private final String randomString = RandomStringUtils.randomAlphanumeric(10);
    private String email;

    @BeforeEach
    void setUp() {
        email = randomString+"@email.com";
        registrationRequest = new RegistrationRequest(
                "Nombre",
                "Apellido",
                email,
                "password"
        );
    }

    @Test
    void registerNewUser() throws Exception {
    	MockMultipartFile file = new MockMultipartFile("image", "image.png", "text/plain", "image data".getBytes());
    	RegistrationRequest request = new RegistrationRequest("John", "Wick", "babayaga@gmail.com", "daisy");
    	request.setPhoto(file);
        mockMvc.perform(multipart("/auth/register")
        				.file(file)
                        .param("firstName", request.getFirstName())
        				.param("lastName", request.getLastName())
        				.param("email", request.getEmail())
        				.param("password", request.getPassword()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void tryToRegistryNewUserWithAnExistingEmail() throws Exception {
        User user = userRepository.findAll().stream().findFirst().get();
        registrationRequest.setEmail(user.getEmail());
        mockMvc.perform(multipart("/auth/register")
		        		.param("firstName", user.getFirstName())
						.param("lastName", user.getLastName())
						.param("email", user.getEmail())
						.param("password", user.getPassword()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tryToLoginWithNotExistingUser() throws Exception {
        User user = userRepository.findAll().stream().findFirst().get();
        loginRequest.setPassword(user.getPassword());
        loginRequest.setEmail(user.getEmail());
        mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content(mapObjToJson(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithExistingUser() throws Exception {
        authService.register(registrationRequest);
        loginRequest.setPassword(registrationRequest.getPassword());
        loginRequest.setEmail(registrationRequest.getEmail());

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjToJson(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void tryToGetInfoOfUserWithoutToken() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@email.com",roles = "ADMIN")
    void getInfoOfUserAsAdmin() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@email.com")
    void getInfoOfUserAsUser() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public <T> String mapObjToJson(T entity) throws JsonProcessingException {
        return objectMapper.writeValueAsString(entity);
    }
}