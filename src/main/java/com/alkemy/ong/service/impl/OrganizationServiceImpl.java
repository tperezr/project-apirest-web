package com.alkemy.ong.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alkemy.ong.dto.OrganizationRequest;
import com.alkemy.ong.dto.OrganizationResponse;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.model.Organization;
import com.alkemy.ong.repository.OrganizationRepository;
import com.alkemy.ong.service.OrganizationService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

	private final MessageSource messageSource;
	private final OrganizationRepository organizationRepository;
	private final AmazonClient amazonClient;
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganizationResponse> getOrganizations() {
		return organizationRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationResponse getOrganization(Long id) {
		Optional<Organization> response = organizationRepository.findById(id);
		String message = messageSource.getMessage("error.entity.not-found-id", new Object[] {"Organization",id}, Locale.US);
		return mapToDto(response.orElseThrow(() -> new EntityNotFoundException(message)));
	}
	
	@Override
	@Transactional
	public OrganizationResponse updateOrganization(Long id, OrganizationRequest organizationRequest) {
		
		Organization ong;
		if (id == null)
			ong = organizationRepository.findAll().stream().findFirst().orElseThrow(this::makeEntityNotFoundException);
		else
			ong = organizationRepository.findById(id).orElseThrow(this::makeEntityNotFoundException);
		
		if (organizationRequest.hasName())
			ong.setName(organizationRequest.getName());
		if (organizationRequest.hasImage()) {
			amazonClient.deleteFileFromS3Bucket(ong.getImage());
			ong.setImage(amazonClient.uploadFile(organizationRequest.getImage()));
		}
		if (organizationRequest.hasEmail())
			ong.setEmail(organizationRequest.getEmail());
		if (organizationRequest.hasWelcomeText())
			ong.setWelcomeText(organizationRequest.getWelcomeText());
		if (organizationRequest.hasAddress())
			ong.setAddress(organizationRequest.getAddress());
		if (organizationRequest.hasPhone())
			ong.setPhone(organizationRequest.getPhone());
		if (organizationRequest.hasAboutUs())
			ong.setAboutUs(organizationRequest.getAboutUs());
		if (organizationRequest.hasUrlFacebook())
			ong.setUrlFacebook(organizationRequest.getUrlFacebook());
		if (organizationRequest.hasUrlLinkedin())
			ong.setUrlLinkedin(organizationRequest.getUrlLinkedin());
		if (organizationRequest.hasUrlInstagram())
			ong.setUrlInstragram(organizationRequest.getUrlInstagram());
		return mapToDto(ong);
	}
	
	private OrganizationResponse mapToDto(Organization organization) {
		return OrganizationResponse.builder()
				.name(organization.getName())
				.image(organization.getImage())
				.phone(organization.getPhone())
				.address(organization.getAddress())
				.urlFacebook(organization.getUrlFacebook())
				.urlInstagram(organization.getUrlInstragram())
				.urlLinkedin(organization.getUrlLinkedin())
				.build();
	}
	
	private EntityNotFoundException makeEntityNotFoundException() {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] {"Organization"}, Locale.US);
		return new EntityNotFoundException(error);
	}

}
