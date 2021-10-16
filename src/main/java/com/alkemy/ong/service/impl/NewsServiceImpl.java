package com.alkemy.ong.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alkemy.ong.dto.NewsCommentsRes;
import com.alkemy.ong.dto.NewsRequest;
import com.alkemy.ong.dto.NewsResponse;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.model.Category;
import com.alkemy.ong.model.Comment;
import com.alkemy.ong.model.News;
import com.alkemy.ong.repository.CategoryRepository;
import com.alkemy.ong.repository.NewsRepository;

import com.alkemy.ong.service.NewsService;

@Service
public class NewsServiceImpl implements NewsService {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	NewsRepository newsRepository;
	@Autowired
	private AmazonClient amazonClient;
	@Autowired
	private MessageSource messageSource;

	@Override
	public String insert(NewsRequest news) throws Exception {
		String message = messageSource.getMessage("news.error.category", new Object[]{"New"}, Locale.US);
		Category category = categoryRepository.findById(news.getCategoryId()).orElse(null);
		if(category == null) throw new Exception(message);
		try {
			String urlImage = amazonClient.uploadFile(news.getImage());
			newsRepository.save(
					new News(news.getName(), news.getContent(),
							urlImage,category)
					);
			message = messageSource.getMessage("entity.created", new Object[]{"New"}, Locale.US);
		} catch (Exception e) {
			message = messageSource.getMessage("entity.created.error", new Object[]{"New"}, Locale.US);
			throw new Exception(message);
		}
		return message;
	}

	@Override
	public String delete(Long id) throws Exception {
		News news = newsRepository.findById(id).orElse(null);
		String message = messageSource.getMessage("entity.deleted.error", new Object[]{"New"}, Locale.US);
	
		if(news==null) throw new Exception(message);
		
		try {
			newsRepository.deleteById(id);
			message = messageSource.getMessage("entity.deleted", new Object[]{"New"}, Locale.US);
		}
		catch(Exception e) {
			message = messageSource.getMessage("error.entity.invalid-id", new Object[]{}, Locale.US);
			throw new Exception(message);
		}
		return message;
	}
	
	@Override
	public NewsResponse findNewsById(Long id) {
		Optional<News> news = newsRepository.findById(id);
        if (news.isPresent()){
            return this.mapToDto(news.get());
        }
        else {
            String notFoundMsg = messageSource.getMessage("error.entity.not-found",new Object[]{"New"}, Locale.US);
            throw new EntityNotFoundException(notFoundMsg);
        }
	}

	@Override
	public ResponseEntity<?> updateById(NewsRequest updatedNews, Long id) {

		Optional<News> originNews = newsRepository.findById(id);

		if (originNews.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage("news.error.notFound", new Object[]{"New"}, Locale.US));
		}
		try {

			if (updatedNews.getImage() != null) {
				amazonClient.deleteFileFromS3Bucket(originNews.get().getImage());
				String urlImage = amazonClient.uploadFile(updatedNews.getImage());
				originNews.get().setImage(urlImage);
			}
			originNews.get().setName(updatedNews.getName());
			originNews.get().setContent(updatedNews.getContent());

			Category category =categoryRepository.findById(updatedNews.getCategoryId()).orElse(null);
			String message = messageSource.getMessage("news.error.category", new Object[]{"New"}, Locale.US);
			if(category == null) throw new Exception(message);
			originNews.get().setCategoryId(category);

			newsRepository.save(originNews.get());

			return ResponseEntity.ok(originNews.get());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource.getMessage("news.error.cantUpdate", new Object[]{"New"}, Locale.US));
		}

	}

	@Override
	public	List<NewsCommentsRes> getComments(Long id){
		return newsRepository.findById(id).get().getComents().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	private NewsCommentsRes mapToDto(Comment comment){
		return NewsCommentsRes.builder()
			.username(comment.getUser().getEmail())
			.body(comment.getBody())
			.createdAt(comment.getCreatedAt())
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NewsResponse> findAll(Pageable pageReqest) {
		return newsRepository.findAll(pageReqest).map(this::mapToDto);
	}
	
	private NewsResponse mapToDto(News news) {
		return NewsResponse.builder()
				.name(news.getName())
				.content(news.getContent())
				.image(news.getImage())
				.categoryName(news.getCategoryId().getName())
				.build();
	}
	
}
