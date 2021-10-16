package com.alkemy.ong.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.alkemy.ong.dto.OrganizationRequest;
import com.alkemy.ong.dto.OrganizationResponse;
import com.alkemy.ong.service.OrganizationService;
import com.alkemy.ong.service.impl.UserServiceImpl;
import com.alkemy.ong.util.JwtUtil;

@WebMvcTest(controllers = OrganizationController.class)
public class OrganizationControllerTest {
	
	
	@MockBean
	private OrganizationService organizationService;
	
	@MockBean
    private UserServiceImpl userService;
	
	@MockBean
	private JwtUtil jwtUtil;
	
	@MockBean
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MessageSource messageSource;
	
	private static final OrganizationRequest ongRequest = new OrganizationRequest();
	private static final OrganizationResponse ongResponse = new OrganizationResponse();
	
	@BeforeAll
	public static void setUp() {
		
		MockMultipartFile file = new MockMultipartFile("image", "image.png", "text/plain", "image data".getBytes());
		ongRequest.setName("Test ONG");
		ongRequest.setImage(file);
		ongRequest.setEmail("test@email.com");
		ongRequest.setPhone(55555555);
		ongRequest.setWelcomeText("Welcome Text");
		ongRequest.setAboutUs("About Us");
		ongRequest.setAddress("Test Street 1234");
		ongRequest.setUrlFacebook("facebook");
		ongRequest.setUrlInstagram("instagram");
		ongRequest.setUrlLinkedin("linkedin");
		
		ongResponse.setName("Somos Más");
		ongResponse.setImage("https://alkemy-56-bucket.s3.sa-east-1.amazonaws.com/logo-ong.png");
		ongResponse.setAddress("Calle 1, Provincia");
		ongResponse.setPhone(1160112988);
		ongResponse.setUrlFacebook("https://es-la.facebook.com/somosmas");
		ongResponse.setUrlInstagram("https://www.instagram.com/somosmas");
		ongResponse.setUrlLinkedin("https://www.linkedin.com/in/somosmas");
		
	}
	
	@Test
	@DisplayName("Should show ong public information to admin user")
	@WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
	public void shouldShowOngInformationToAdminUser() throws Exception {
		Mockito.when(organizationService.getOrganizations()).thenReturn(List.of(ongResponse));
		mockMvc.perform(get("/organization/public"))
			   .andExpect(status().isOk())
			   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
			   .andExpect(jsonPath("$.size()", Matchers.is(1)))
			   .andExpect(jsonPath("$[0].name", Matchers.is(ongResponse.getName())))
			   .andExpect(jsonPath("$[0].image", Matchers.is(ongResponse.getImage())))
			   .andExpect(jsonPath("$[0].address", Matchers.is(ongResponse.getAddress())))
			   .andExpect(jsonPath("$[0].phone", Matchers.is(ongResponse.getPhone())))
			   .andExpect(jsonPath("$[0].urlFacebook", Matchers.is(ongResponse.getUrlFacebook())))
			   .andExpect(jsonPath("$[0].urlInstagram", Matchers.is(ongResponse.getUrlInstagram())))
			   .andExpect(jsonPath("$[0].urlLinkedin", Matchers.is(ongResponse.getUrlLinkedin())));
	}
	
	@Test
	@DisplayName("Should show ong public information to regular user")
	@WithMockUser(username = "testuser@email.com", roles = {"USER"})
	public void shouldShowOngInformationToRegularUser() throws Exception {
		Mockito.when(organizationService.getOrganizations()).thenReturn(List.of(ongResponse));
		mockMvc.perform(get("/organization/public"))
			   .andExpect(status().isOk())
			   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
			   .andExpect(jsonPath("$.size()", Matchers.is(1)))
			   .andExpect(jsonPath("$[0].name", Matchers.is(ongResponse.getName())))
			   .andExpect(jsonPath("$[0].image", Matchers.is(ongResponse.getImage())))
			   .andExpect(jsonPath("$[0].address", Matchers.is(ongResponse.getAddress())))
			   .andExpect(jsonPath("$[0].phone", Matchers.is(ongResponse.getPhone())))
			   .andExpect(jsonPath("$[0].urlFacebook", Matchers.is(ongResponse.getUrlFacebook())))
			   .andExpect(jsonPath("$[0].urlInstagram", Matchers.is(ongResponse.getUrlInstagram())))
			   .andExpect(jsonPath("$[0].urlLinkedin", Matchers.is(ongResponse.getUrlLinkedin())));
	}
	
	@Test
	@DisplayName("Should forbid showing ong public information to anonymous user")
	@WithAnonymousUser
	public void shouldForbidShowingOngInformationToAnonymousUser() throws Exception {
		mockMvc.perform(get("/organization/public"))
			   .andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should show that no records has found")
	@WithMockUser(username = "testuser@email.com", roles = {"ADMIN", "USER"})
	public void shouldShowNoRecordsFound() throws Exception {
		Mockito.when(organizationService.getOrganizations()).thenReturn(new ArrayList<OrganizationResponse>());
		String message = messageSource.getMessage("no.records.found", new Object[]{"Organization"}, Locale.US);
		mockMvc.perform(get("/organization/public"))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
		       .andExpect(content().string(equalTo(message)));
	}
	
	@Test
	@DisplayName("Should update ong public information as admin user")
	@WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
	public void shouldUpdateOngInformationAsAdminUser() throws Exception {
		Mockito.when(organizationService.updateOrganization(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mapToDto(ongRequest));
		mockMvc.perform(multipart("/organization/public")
							.file("image", ongRequest.getImage().getBytes())
							.param("name", ongRequest.getName())
							.param("email", ongRequest.getEmail())
							.param("welcomeText", ongRequest.getWelcomeText())
							.param("phone", String.valueOf(ongRequest.getPhone()))
							.param("address", ongRequest.getAddress())
							.param("aboutUs", ongRequest.getAboutUs())
							.param("urlFacebook", ongRequest.getUrlFacebook())
							.param("urlInstagram", ongRequest.getUrlInstagram())
							.param("urlLinkedin", ongRequest.getUrlLinkedin()))
			   .andExpect(status().isOk())
			   .andExpect(content().contentType("application/json"))
			   .andExpect(jsonPath("$.name", Matchers.is(ongRequest.getName())))
		       .andExpect(jsonPath("$.image", Matchers.is(ongRequest.getImage().getOriginalFilename())))
		       .andExpect(jsonPath("$.phone", Matchers.is(ongRequest.getPhone())))
		       .andExpect(jsonPath("$.address", Matchers.is(ongRequest.getAddress())))
		       .andExpect(jsonPath("$.urlFacebook", Matchers.is(ongRequest.getUrlFacebook())))
		       .andExpect(jsonPath("$.urlInstagram", Matchers.is(ongRequest.getUrlInstagram())))
		       .andExpect(jsonPath("$.urlLinkedin", Matchers.is(ongRequest.getUrlLinkedin())));
	}
	
	@Test
	@DisplayName("Should throw bad request when try to update ong public information with empty data")
	@WithMockUser(username = "testuser@email.com", roles = {"ADMIN"})
	public void shouldThrowBadRequestWhenUpdatingOngWhithEmptyData() throws Exception {
		mockMvc.perform(multipart("/organization/public"))
		       .andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should forbid update ong public information as regular user")
	@WithMockUser(username = "testuser@email.com", roles = {"USER"})
	public void shouldForbidUpdateOngInformationAsRegularUser() throws Exception {
		mockMvc.perform(multipart("/organization/public"))
			   .andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should forbid update ong public information as anonymous user")
	@WithAnonymousUser
	public void shouldForbidUpdateOngInformationAsAnonymousUser() throws Exception {
		mockMvc.perform(multipart("/organization/public"))
			   .andExpect(status().isForbidden());
	}
	
	private OrganizationResponse mapToDto(OrganizationRequest request) {
		return OrganizationResponse.builder()
				.name(request.getName())
				.image(request.getImage().getOriginalFilename())
				.phone(request.getPhone())
				.address(request.getAddress())
				.urlFacebook(request.getUrlFacebook())
				.urlInstagram(request.getUrlInstagram())
				.urlLinkedin(request.getUrlLinkedin())
				.build();
	}
	
}
