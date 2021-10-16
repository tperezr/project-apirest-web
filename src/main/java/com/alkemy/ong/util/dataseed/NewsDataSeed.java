package com.alkemy.ong.util.dataseed;

import org.springframework.stereotype.Component;

import com.alkemy.ong.exception.ONGException;
import com.alkemy.ong.model.Category;
import com.alkemy.ong.model.News;
import com.alkemy.ong.repository.CategoryRepository;
import com.alkemy.ong.repository.NewsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsDataSeed {
	
	private static final int numberOfNews = 5;

	private final NewsRepository newsRepository;
	private final CategoryRepository categoryRepository;
	
	public void loadNewsData() {
		if (newsDataIsEmpty()) {
			makeNews();
		} else
			log.info("Category data is not empty");
	}
	
	private boolean newsDataIsEmpty() {
		return newsRepository.count() == 0;
	}
	
	private void makeNews() {
		Category category = categoryRepository.findById(1L).orElseThrow(() -> new ONGException("Category id 1 not found"));
		for (int i = 1; i <= numberOfNews; i++) {
			News news = new News();
			news.setName("News " + i);
			news.setContent("Content for news " + i);
			news.setImage("https://alkemy-56-bucket.s3.sa-east-1.amazonaws.com/logo-ong.png");
			news.setCategoryId(category);
			newsRepository.save(news);
			log.info("Added news: {}", news.getName());
		}
		
	}
	
}
