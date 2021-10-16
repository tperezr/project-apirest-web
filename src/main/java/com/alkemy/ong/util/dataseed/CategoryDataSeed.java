package com.alkemy.ong.util.dataseed;

import org.springframework.stereotype.Component;

import com.alkemy.ong.model.Category;
import com.alkemy.ong.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDataSeed {
	
	private static final int numberOfCategories = 30;
	
	private final CategoryRepository categoryRepository;
	
	public void loadCategoryData() {
		if (categoryDataIsEmpty()) {
			makeCategories();
		} else
			log.info("Category data is not empty");
	}
	
	private boolean categoryDataIsEmpty() {
		return categoryRepository.count() == 0;
	}
	
	private void makeCategories() {
		for (int i = 1; i <= numberOfCategories; i++) {
			Category category = new Category();
			category.setName("Category " + i);
			category.setDescription("Test category");
			category.setImage("https://alkemy-56-bucket.s3.sa-east-1.amazonaws.com/logo-ong.png");
			categoryRepository.save(category);
			log.info("Added category: {}", category.getName());
		}
		
	}

}
