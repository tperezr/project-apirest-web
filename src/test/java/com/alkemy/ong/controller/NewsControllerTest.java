package com.alkemy.ong.controller;

import com.alkemy.ong.dto.NewsRequest;
import com.alkemy.ong.model.Category;
import com.alkemy.ong.model.News;
import com.alkemy.ong.repository.NewsRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class NewsControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private NewsRepository newsRepository;
	@Autowired
	private ObjectMapper objectMapper;
	private final NewsRequest news = new NewsRequest();
	private final Category category = new Category();
	private MockMultipartHttpServletRequestBuilder mockMvcRequestBuilders;

	@BeforeEach
	void setUp() {

	    MockMultipartFile file = new MockMultipartFile("image", "image.png", TEXT_PLAIN_VALUE, "image.png".getBytes());
		news.setName("Name");
		news.setContent("Content");
		news.setImage(file);
		category.setName("Informativo");
		category.setId(1L);
		category.setDescription("Description");
		category.setImage("https://alkemy-56-bucket.s3.sa-east-1.amazonaws.com/exampleCategory.jpg");

		news.setCategoryId(category.getId());
	}

	@Test
	@WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
	void createNewsAsAnAdmin() throws Exception {
		mockMvc.perform(multipart("/news")
				.file((MockMultipartFile) news.getImage())
				.param("name", news.getName())
				.param("content", news.getContent())
				.param("categoryId", String.valueOf(news.getCategoryId())))
		.andDo(print())
		.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "user01@email.com")
	void createNewsAsAnUser() throws Exception {
		mockMvc.perform(multipart("/news")
				.file((MockMultipartFile) news.getImage())
				.param("name", news.getName())
				.param("content", news.getContent())
				.param("categoryId", String.valueOf(news.getCategoryId())))
		.andDo(print())
		.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
	void tryToCreateNewsWithEmptyBody() throws Exception {
		mockMvc.perform(multipart("/news"))
		.andDo(print())
		.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
	void tryToCreateNewsWithAnEmptyField() throws Exception {
		news.setName(null);
		mockMvc.perform(multipart("/news")
				.file((MockMultipartFile) news.getImage())
				.param("name", news.getName())
				.param("content", news.getContent())
				.param("categoryId", String.valueOf(news.getCategoryId())))
		.andDo(print())
		.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
	void updateNewsAsAnAdmin() throws Exception {
		News newsToUpdate = newsRepository.findAll().stream().findFirst().get();
		newsToUpdate.setContent("AnotherContent");
		
		mockMvcRequestBuilders = MockMvcRequestBuilders.multipart("/news/{id}",newsToUpdate.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});
		mockMvc.perform(mockMvcRequestBuilders
				.file((MockMultipartFile) news.getImage())
				.param("name", newsToUpdate.getName())
				.param("content", news.getContent())
				.param("categoryId", String.valueOf(news.getCategoryId())))
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user01@email.com")
	void updateNewsAsAnUser() throws Exception {
		News newsToUpdate = newsRepository.findAll().stream().findFirst().get();
		newsToUpdate.setContent("AnotherContent");
		
		mockMvcRequestBuilders = MockMvcRequestBuilders.multipart("/news/{id}",newsToUpdate.getId());
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});
		mockMvc.perform(mockMvcRequestBuilders
				.param("name", newsToUpdate.getName()))
		.andDo(print())
		.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
	void tryToUpdateNotExistingNews() throws Exception {
		mockMvcRequestBuilders = MockMvcRequestBuilders.multipart("/news/-1");
        mockMvcRequestBuilders.with(request -> {request.setMethod("PUT"); return request;});
		mockMvc.perform(mockMvcRequestBuilders
				.file((MockMultipartFile) news.getImage())
				.param("name", news.getName())
				.param("content", news.getContent())
				.param("categoryId", String.valueOf(news.getCategoryId())))
		.andDo(print())
		.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin01@email.com", roles = {"ADMIN"})
	void createNewsWithNotExitingCategory() throws Exception {
		mockMvc.perform(multipart("/news")
				.file((MockMultipartFile) news.getImage())
				.param("name", news.getName())
				.param("content", news.getContent())
				.param("categoryId", String.valueOf(-1)))
		.andDo(print())
		.andExpect(status().isConflict());
	}
}
