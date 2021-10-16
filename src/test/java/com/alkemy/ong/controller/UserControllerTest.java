package com.alkemy.ong.controller;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.model.Role;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.service.IUserService;
import com.alkemy.ong.service.impl.UserServiceImpl;
import com.alkemy.ong.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private static final UserDTO user_user = new UserDTO();
    private static final UserDTO user_admin = new UserDTO();
    private static final Role role_user = new Role();
    private static final Role role_admin = new Role();
    private static final List<UserDTO> responseUserList = new ArrayList<>();

    private  static final User user = new User();

    @BeforeEach
    void setUp() {
        role_user.setId(1L);
        role_user.setName("USER");
        role_user.setDescription("USER");

        role_admin.setName("ADMIN");
        role_admin.setDescription("ADMIN");

        user_user.setFirstName("FirsNameUser");
        user_user.setLastName("LastNameUser");
        user_user.setEmail("user_user@email.com");
        user_user.setPhoto("http://Photo");
        user_user.setRoleId(role_user);

        user_admin.setFirstName("FirsNameAdmin");
        user_admin.setLastName("LastNameAdmin");
        user_admin.setEmail("user_admin@email.com");
        user_admin.setPhoto("http://PhotoAdmin");
        user_admin.setRoleId(role_admin);

        user.setId(1L);
        user.setFirstName("FirsNameAdmin");
        user.setLastName("LastNameAdmin");
        user.setPassword("1234");
        user.setEmail("user_admin@email.com");
        user.setPhoto("http://PhotoAdmin");
        user.setRoleId(role_admin);

        responseUserList.add(user_user);
        responseUserList.add(user_admin);
    }

    @Test
    @DisplayName("[GET][OK]-> Should show the users list to admin user")
    @WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
    void getUserList() throws Exception {
        Mockito.when(userService.findAllUsers()).thenReturn(responseUserList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", Matchers.is(responseUserList.size())))
                .andExpect(jsonPath("$[0].firstName", Matchers.is(user_user.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", Matchers.is(user_user.getLastName())))
                .andExpect(jsonPath("$[0].email", Matchers.is(user_user.getEmail())))
                .andExpect(jsonPath("$[0].photo", Matchers.is(user_user.getPhoto())))
                .andExpect(jsonPath("$[0].roleId.name", Matchers.is(user_user.getRoleId().getName())))
                .andExpect(jsonPath("$[0].roleId.description", Matchers.is(user_user.getRoleId().getDescription())))
                .andExpect(jsonPath("$[1].firstName", Matchers.is(user_admin.getFirstName())))
                .andExpect(jsonPath("$[1].lastName", Matchers.is(user_admin.getLastName())))
                .andExpect(jsonPath("$[1].email", Matchers.is(user_admin.getEmail())))
                .andExpect(jsonPath("$[1].photo", Matchers.is(user_admin.getPhoto())))
                .andExpect(jsonPath("$[1].roleId.name", Matchers.is(user_admin.getRoleId().getName())))
                .andExpect(jsonPath("$[1].roleId.description", Matchers.is(user_admin.getRoleId().getDescription())));

    }

    @Test
    @DisplayName("[GET][NOT ADMIN]-> Should show forbidden do not have the authority")
    @WithMockUser(username = "testuser@email.com", roles = {"User"})
    void getUserListNotAdmin() throws Exception {
        Mockito.when(userService.findAllUsers()).thenReturn(responseUserList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[GET][NOT LOGIN]-> Should show forbidden")
    @WithAnonymousUser
    void getUserListNotLogin() throws Exception {
        Mockito.when(userService.findAllUsers()).thenReturn(responseUserList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[PUT][OK]-> Should show a message if the delete was successfully")
    @WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
    void deleteUser() throws Exception{
        String message_userDeleted = messageSource.getMessage("user.deleted", new Object[]{"User"}, Locale.US);
        Mockito.when(userService.softDeleteUser(1)).thenReturn(message_userDeleted);
        mockMvc.perform(put("/users/{id}",1))
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(message_userDeleted)));
    }

    @Test
    @DisplayName("[PUT][NOT FOUND]-> Should show a message user not found")
    @WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
    void deleteUserNotfoud() throws Exception{
        String message_userNotFound = messageSource.getMessage("user.not.found.by.id", new Object[]{"User"}, Locale.US);
        Mockito.when(userService.softDeleteUser(10)).thenThrow( new UsernameNotFoundException(message_userNotFound));
        mockMvc.perform(put("/users/{id}",10))
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", Matchers.is(message_userNotFound)));
    }

    @Test
    @DisplayName("[PUT][NOT ADMIN]-> Should show a message user do not have the required authority")
    @WithMockUser(username = "testuser@email.com", roles = {"USER"})
    void deleteUserNotAdmin() throws Exception{
        mockMvc.perform(put("/users/{id}",1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[PUT][NOT LOGIN]-> Should show a message is forbidden")
    @WithAnonymousUser
    void deleteUserNotLogin() throws Exception{
        mockMvc.perform(put("/users/{id}",1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[PUT][NOT ID]-> Should show Method not allowed")
    @WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
    void deleteUserNotId() throws Exception{
        mockMvc.perform(put("/users/"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("[PATCH][OK]-> Should show the users list to admin user")
    @WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
    void updateUser() throws Exception{

        Mockito.when(userService.updateUser(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(user);
        mockMvc.perform(patch("/users/{id}", 1).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstName", Matchers.is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", Matchers.is(user.getLastName())))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())))
                .andExpect(jsonPath("$.password", Matchers.is(user.getPassword())))
                .andExpect(jsonPath("$.photo", Matchers.is(user.getPhoto())))
                .andExpect(jsonPath("$.roleId.name", Matchers.is(user.getRoleId().getName())))
                .andExpect(jsonPath("$.roleId.description", Matchers.is(user.getRoleId().getDescription())));
    }

    @Test
    @DisplayName("[PATCH][NOT ADMIN]->Should show forbidden don have the authority")
    @WithMockUser(username = "testuser@email.com", roles = {"USER"})
    void updateUserNotAdmin() throws Exception{
        Mockito.when(userService.updateUser(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(user);
        mockMvc.perform(patch("/users/{id}", 1).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[PATCH][NOT LOGIN]->Should show forbidden")
    @WithAnonymousUser
    void updateUserNotLogin() throws Exception{
        Mockito.when(userService.updateUser(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(user);
        mockMvc.perform(patch("/users/{id}", 1).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }


}