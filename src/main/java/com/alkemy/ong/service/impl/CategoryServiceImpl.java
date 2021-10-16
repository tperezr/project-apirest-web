package com.alkemy.ong.service.impl;

import java.util.List;
import java.util.Locale;

import com.alkemy.ong.dto.*;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.model.Category;
import com.alkemy.ong.repository.CategoryRepository;
import com.alkemy.ong.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	
	private final MessageSource messageSource;
    private final CategoryRepository categoryRepository;
    private final AmazonClient amazonClient;
    
	private static final int PAGE_SIZE = 10;
    
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(this::makeEntityNotFoundException);
        return this.mapToDto(category);
    }


    @Override
	public String delete(Long id) throws Exception {
		Category category = categoryRepository.findById(id).orElse(null);
		String message;
		if (category == null) {
			message = messageSource.getMessage("entity.deleted.error", new Object[]{"Category"}, Locale.US);
			throw new Exception(message);
		}
		
		try {
			categoryRepository.deleteById(id);
			message = messageSource.getMessage("entity.deleted", new Object[]{"Category"}, Locale.US);
		}
		catch(Exception e) {
			message = messageSource.getMessage("error.entity.invalid-id", new Object[]{}, Locale.US);
			throw new Exception(message);
		}
		return message;
	}
    @Override
    public String save(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        try {
        	category.setImage(amazonClient.uploadFile(categoryRequest.getImage()));
            categoryRepository.save(category);
            return messageSource.getMessage("entity.created", new Object[]{"Category"}, Locale.US);
        }
        catch(Exception e) {
            return messageSource.getMessage("error.add", new Object[]{"Category"}, Locale.US);
        }
        
    }
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllname() {
		return categoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }
	
	@Override
	@Transactional
	public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
		Category category = categoryRepository.findById(id).orElseThrow(this::makeEntityNotFoundException);
		if (categoryRequest.hasName())
			category.setName(categoryRequest.getName());
		if (categoryRequest.hasDescription())
			category.setDescription(categoryRequest.getDescription());
		if (categoryRequest.hasImage() && category.getImage() != null) {
			amazonClient.deleteFileFromS3Bucket(category.getImage());
			category.setImage(amazonClient.uploadFile(categoryRequest.getImage()));
		}
		return mapToDto(category);
	}
	
	private CategoryResponse mapToDto(Category category) {
		CategoryResponse response = new CategoryResponse();
		response.setName(category.getName());
		response.setDescription(category.getDescription());
		response.setImage(category.getImage());
		return response;
	}
	
	private EntityNotFoundException makeEntityNotFoundException() {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] {"Category"}, Locale.US);
		return new EntityNotFoundException(error);
	}

	@Override
	public PageDto<CategoryResponse> getCategoriesByPage(int page, UriComponentsBuilder uriBuilder) {
		Pageable pageable = PageRequest.of(page,PAGE_SIZE);
		Page<Category> categoriesPage = categoryRepository.findAll(pageable);
		PageDto<CategoryResponse> pageDto = new PageDto<>();

		if(!(page == 0)){
			pageDto.getLinks().put("prev",buildUrlPage(page-1,uriBuilder));
		}
		if(!(page == categoriesPage.getTotalPages()-1)){
			pageDto.getLinks().put("next",buildUrlPage(page+1,uriBuilder));
		}
		pageDto.setContent(mapCategoriesToDto(categoriesPage.getContent()));
		return pageDto;
	}

	public String buildUrlPage(int page,UriComponentsBuilder uriBuilder){
		return uriBuilder.toUriString() + "/categories?page=" + page;
	}

	public List<CategoryResponse> mapCategoriesToDto(List<Category> categories){
		return categories.stream().map(this::mapToDto).collect(Collectors.toList());
	}
}
