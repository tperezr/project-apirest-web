package com.alkemy.ong.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alkemy.ong.dto.OrganizationRequest;
import com.alkemy.ong.dto.OrganizationResponse;
import com.alkemy.ong.exception.ONGBadRequestException;
import com.alkemy.ong.service.OrganizationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Organization")
@RestController
@AllArgsConstructor
@RequestMapping("/organization")
public class OrganizationController {
	
	private final MessageSource messageSource;
	private final OrganizationService organizationService;
	
	@GetMapping("/public")
	public ResponseEntity<?> getOrganizations() {
		List<OrganizationResponse> organizations = organizationService.getOrganizations();
		if(organizations.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(messageSource.getMessage("no.records.found", new Object[]{"Organization"}, Locale.US));
		return ResponseEntity.status(HttpStatus.OK).body(organizations);
	}
	
	@PostMapping(value = {"/public"}, consumes = {"multipart/form-data"})
	public ResponseEntity<OrganizationResponse> updateOrganization(@ModelAttribute OrganizationRequest organizationRequest) {
		if (organizationRequest.hasNoInfo())
			throw new ONGBadRequestException(messageSource.getMessage("error.request-body", null, Locale.US));
		return ResponseEntity.status(HttpStatus.OK).body(organizationService.updateOrganization(null, organizationRequest));
	}
	
}
