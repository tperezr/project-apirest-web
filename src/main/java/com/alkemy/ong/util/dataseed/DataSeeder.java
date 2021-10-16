package com.alkemy.ong.util.dataseed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class DataSeeder implements CommandLineRunner {
	
	private final OngDataSeed ongDataSeed;
	private final UserDataSeed userDataSeed;
	private final CategoryDataSeed categoryDataSeed;
	private final NewsDataSeed newsDataSeed;

	@Override
	public void run(String... args) throws Exception {
		log.info("Data seed start");
		ongDataSeed.loadOrganizationData();
		userDataSeed.loadUserData();
		categoryDataSeed.loadCategoryData();
		//newsDataSeed.loadNewsData();
		log.info("Data seed finish");
	}
	
}
