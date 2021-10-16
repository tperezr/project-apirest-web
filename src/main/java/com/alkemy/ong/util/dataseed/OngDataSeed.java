package com.alkemy.ong.util.dataseed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alkemy.ong.model.Organization;
import com.alkemy.ong.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OngDataSeed {
	
	@Value("${dataseed.ong.name}")
	private String name;
	
	@Value("${dataseed.ong.image}")
	private String image;
	
	@Value("${dataseed.ong.email}")
	private String email;
	
	@Value("${dataseed.ong.phone}")
	private int phone;
	
	@Value("${dataseed.ong.welcome-text}")
	private String welcomeText;
	
	@Value("${dataseed.ong.facebook}")
	private String facebook;
	
	@Value("${dataseed.ong.instagram}")
	private String instagram;
	
	@Value("${dataseed.ong.linkedin}")
	private String linkedin;
	
	private final OrganizationRepository organizationRepository;
	
	public void loadOrganizationData() {
		if (organizationDataIsEmpty())
			saveOng();
		else
			log.info("ONG data is not empty");
	}
	
	private boolean organizationDataIsEmpty() {
		return organizationRepository.count() == 0;
	}
	
	private void saveOng() {
		Organization ong = new Organization();
		ong.setName(name);
		ong.setImage(image);
		ong.setEmail(email);
		ong.setPhone(phone);
		ong.setWelcomeText(welcomeText);
		ong.setUrlFacebook(facebook);
		ong.setUrlInstragram(instagram);
		ong.setUrlLinkedin(linkedin);
		ong = organizationRepository.save(ong);
		log.info("Added organization: {}", ong);
	}

}
