package com.alkemy.ong.controller;

import com.alkemy.ong.dto.TestimonialRequest;
import com.alkemy.ong.model.Testimonials;
import com.alkemy.ong.repository.TestimonialRepository;
import com.alkemy.ong.service.TestimonialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestimonialControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Mock
    private TestimonialRepository testimonialRepositoryMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    TestimonialService testimonialService;

    private Testimonials testimonials;
    private MockMultipartFile imageFile;
    private final TestimonialRequest testimonialRequest = new TestimonialRequest();
    private MockMultipartHttpServletRequestBuilder mockMvcRequestBuilders;


    @BeforeEach
    void setUp(){
        imageFile = new MockMultipartFile("image", "image.png", TEXT_PLAIN_VALUE, "image.png".getBytes());
        testimonialRequest.setName("Name");
        testimonialRequest.setContent("Content");

        testimonials = new Testimonials();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void createTestimonialAsAdmin() throws Exception {
        mockMvc.perform(multipart("/testimonials")
                        .file(imageFile)
                        .param("name", testimonialRequest.getName())
                        .param("content", testimonialRequest.getContent()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void createTestimonialAsUser() throws Exception {
        mockMvc.perform(multipart("/testimonials")
                        .file(imageFile)
                        .param("name", testimonialRequest.getName())
                        .param("content", testimonialRequest.getContent()))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void createTestimonialWithEmptyBody() throws Exception {
        mockMvc.perform(multipart("/testimonials"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void createTestimonialWithAnEmptyField() throws Exception {
        mockMvc.perform(multipart("/testimonials")
                        .file(imageFile)
                        .param("content",testimonialRequest.getContent()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void updateTestimonialAsAnAdmin() throws Exception {
        Testimonials testimonialsToUpdate = testimonialRepository.findAll().stream().findFirst().get();
        testimonialsToUpdate.setContent("AnotherContent");

        mockMvcRequestBuilders =
                MockMvcRequestBuilders.multipart("/testimonials/{id}",testimonialsToUpdate.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});

        mockMvc.perform(mockMvcRequestBuilders
                        .param("name", testimonialsToUpdate.getName()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateTestimonialAsAnUser() throws Exception {
        Testimonials testimonials = testimonialRepository.findAll().stream().findFirst().get();
        testimonials.setContent("AnotherContent");

        mockMvcRequestBuilders =
                multipart("/testimonials/{id}",testimonials.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});

        mockMvc.perform(mockMvcRequestBuilders)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void tryToUpdateNotExistingTestimonial() throws Exception {
        mockMvcRequestBuilders = MockMvcRequestBuilders.multipart("/testimonials/-1");
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});
        mockMvc.perform(mockMvcRequestBuilders)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deleteTestimonialAsAnAdmin() throws Exception {
        Testimonials testimonialsToDelete = testimonialRepository.findAll().stream().findFirst().get();
        Mockito.when(testimonialService.deleteTestimonialById(testimonialsToDelete.getId())).thenReturn(Boolean.valueOf("SUCCESS")).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.delete("/testimonials/{id}", testimonialsToDelete.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void deleteTestimonialAsAnUser() throws Exception {
        Testimonials testimonialsToDelete = testimonialRepository.findAll().stream().findFirst().get();
        Mockito.when(testimonialService.deleteTestimonialById(testimonialsToDelete.getId())).thenReturn(Boolean.valueOf("SUCCESS")).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.delete("/testimonials/{id}", testimonialsToDelete.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void testimonialByPageTest() throws Exception{
        mockMvc.perform(get("/testimonials?page=1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
