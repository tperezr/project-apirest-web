package com.alkemy.ong.controller;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alkemy.ong.model.Category;
import com.alkemy.ong.repository.CategoryRepository;
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



@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper objectMapper;    
    private MockMultipartFile imageFile;
    private MockMultipartHttpServletRequestBuilder mockMvcRequestBuilders;


    private final Category category = new Category();
    

    @BeforeEach
    void setUp() {
        imageFile = new MockMultipartFile("image","image.png",TEXT_PLAIN_VALUE,"image.png".getBytes());
        category.setName("TestName");
        category.setDescription("TestDescription");
        category.setImage("https://alkemy-56-bucket.s3.sa-east-1.amazonaws.com/example.jpg");
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void createCategoryAsAdmin() throws Exception {
        mockMvc.perform(multipart("/categories")
                .file(imageFile)
                .param("name", category.getName())
                .param("description", category.getDescription()))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username = "user01@email.com")
    void createCategoryAsUser() throws Exception {
        mockMvc.perform(multipart("/categories")
                .file(imageFile)
                .param("name", category.getName())
                .param("description", category.getDescription()))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void createFullEmptyAdmin() throws Exception {
        mockMvc.perform(multipart("/categories"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void createOnlyName() throws Exception {
        mockMvc.perform(multipart("/categories")
                .param("name", category.getName()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void createInvalidName() throws Exception {
        mockMvc.perform(multipart("/categories")
                .param("name", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void getAsAdmin() throws Exception {
        mockMvc.perform(get("/categories"))
        .andDo(print())
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user01@email.com")
    void getAsUser() throws Exception {
        mockMvc.perform(get("/categories"))
        .andDo(print())
        .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void deleteAsAdmin() throws Exception {
        mockMvc.perform(delete("/categories/2"))
        .andDo(print())
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user01@email.com")
    void deleteAsUser() throws Exception {
        mockMvc.perform(delete("/categories/3"))
        .andDo(print())
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void deleteAsAdminNotExisting() throws Exception {
        mockMvc.perform(delete("/categories/2"))
        .andDo(print())
        .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
    void putAsAdminOnlyName() throws Exception {
        Category categoryToUpdate = categoryRepository.findAll().stream().findFirst().get();
        categoryToUpdate.setName("secondName");
        mockMvcRequestBuilders
         = MockMvcRequestBuilders.multipart("/activities/{id}", categoryToUpdate.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});

        mockMvc.perform(mockMvcRequestBuilders
                    .param("name",categoryToUpdate.getName()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user01@email.com" )
    void putAsUser() throws Exception {
        Category categoryToUpdate = categoryRepository.findAll().stream().findFirst().get();
        categoryToUpdate.setName("secondName");
        mockMvcRequestBuilders
         = MockMvcRequestBuilders.multipart("/activities/{id}", categoryToUpdate.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});

        mockMvc.perform(mockMvcRequestBuilders
                    .param("name",categoryToUpdate.getName()))
            .andDo(print())
            .andExpect(status().isForbidden());
    }



    public <T> String mapObjToJson(T entity) throws JsonProcessingException {
        return objectMapper.writeValueAsString(entity);
    }

}
