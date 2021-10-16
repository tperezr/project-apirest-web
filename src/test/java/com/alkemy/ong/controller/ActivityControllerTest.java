package com.alkemy.ong.controller;

import com.alkemy.ong.dto.ActivityRequest;
import com.alkemy.ong.model.Activity;
import com.alkemy.ong.repository.ActivityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ActivityRepository activityRepository;

    private MockMultipartFile imageFile;
    private final ActivityRequest activity =  new ActivityRequest();
    private MockMultipartHttpServletRequestBuilder mockMvcRequestBuilders;

    @BeforeEach
    void setUp() {
        imageFile =
                new MockMultipartFile("image","image.png",TEXT_PLAIN_VALUE,"image.png".getBytes());
        activity.setName("Name");
        activity.setContent("Content");
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void createActivityAsAnAdmin() throws Exception {
        mockMvc.perform(multipart("/activities")
                        .file(imageFile)
                        .param("name", activity.getName())
                        .param("content", activity.getContent()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user01@email.com")
    void createActivityAsAnUser() throws Exception {
        mockMvc.perform(multipart("/activities")
                        .file(imageFile)
                        .param("name", activity.getName())
                        .param("content", activity.getContent()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void tryToCreateActivityWithEmptyBody() throws Exception {
        mockMvc.perform(multipart("/activities"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void tryToCreateActivityWithAnEmptyField() throws Exception {
        mockMvc.perform(multipart("/activities")
                        .file(imageFile)
                        .param("content",activity.getContent()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void updateActivityAsAnAdmin() throws Exception {
        Activity activityToUpdate = activityRepository.findAll().stream().findFirst().get();
        activityToUpdate.setContent("AnotherContent");

        mockMvcRequestBuilders =
                MockMvcRequestBuilders.multipart("/activities/{id}",activityToUpdate.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});

        mockMvc.perform(mockMvcRequestBuilders
                        .param("content",activityToUpdate.getContent()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user01@email.com")
    void updateActivityAsAnUser() throws Exception {
        Activity activityToUpdate = activityRepository.findAll().stream().findFirst().get();
        activityToUpdate.setContent("AnotherContent");

        mockMvcRequestBuilders =
                MockMvcRequestBuilders.multipart("/activities/{id}",activityToUpdate.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});

        mockMvc.perform(mockMvcRequestBuilders)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void tryToUpdateNotExistingActivity() throws Exception {
        mockMvcRequestBuilders = MockMvcRequestBuilders.multipart("/activities/-1");
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});
        mockMvc.perform(mockMvcRequestBuilders)
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}