package com.alkemy.ong.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.alkemy.ong.dto.ContactDto;
import com.alkemy.ong.service.ContactService;
import com.alkemy.ong.service.impl.UserServiceImpl;
import com.alkemy.ong.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ContactController.class)
public class ContactControllerTest {
	
	@MockBean
	private ContactService contactService;
	
	@MockBean
	private UserServiceImpl userService;
	
	@MockBean
	private JwtUtil jwtUtil;
	
	@MockBean
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MessageSource messageSource;
	
	private static final ContactDto contactDto = new ContactDto();
	
	@BeforeAll
	public static void setUp() {
		
		contactDto.setName("Bruno");
		contactDto.setEmail("bruno@gmail.com");
		contactDto.setMessage("Hola soy Bruno");
		contactDto.setPhone("38344842");

	}
	
	@Test
	@WithMockUser(username = "user01@gmail.com")
	void tryToCreateAContactAsAnUser() throws Exception {
		Mockito.when(contactService.saveContact(contactDto)).thenReturn(true);
		mockMvc.perform(post("/contacts")
				.contentType(APPLICATION_JSON)
				.content(mapObjToJson(contactDto)))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "user01@gmail.com",roles = {"ADMIN"})
	void tryToCreateAContactAsAnAdmin() throws Exception {
		Mockito.when(contactService.saveContact(contactDto)).thenReturn(true);
		mockMvc.perform(post("/contacts")
				.contentType(APPLICATION_JSON)
				.content(mapObjToJson(contactDto)))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "user01@gmail.com")
	void tryToCreateAContactAsAnUserWithNoData() throws Exception {
		Mockito.when(contactService.saveContact(contactDto)).thenReturn(true);
		mockMvc.perform(post("/contacts"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@WithMockUser(username = "user01@gmail.com")
	void getAllContactsAsAnUser() throws Exception {
		Mockito.when(contactService.findAllContacts()).thenReturn(List.of(contactDto));
		mockMvc.perform(get("/contacts"))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "user01@gmail.com",roles = {"ADMIN"})
	void getAllContactsAsAnAdmin() throws Exception {
		Mockito.when(contactService.findAllContacts()).thenReturn(List.of(contactDto));
		mockMvc.perform(get("/contacts"))
				.andExpect(status().isOk());
	}
	
	
	 public <T> String mapObjToJson(T entity) throws JsonProcessingException {
	        return objectMapper.writeValueAsString(entity);
	    }
	

}
