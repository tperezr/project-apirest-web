package com.alkemy.ong.service;

import java.util.List;

// import com.alkemy.ong.dto.OrgAndSlideDTO;
import com.alkemy.ong.dto.OrganizationRequest;
import com.alkemy.ong.dto.OrganizationResponse;

public interface OrganizationService {

	List<OrganizationResponse> getOrganizations();
	OrganizationResponse getOrganization(Long id);
	OrganizationResponse updateOrganization(Long id, OrganizationRequest organization);
	
}
