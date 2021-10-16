package com.alkemy.ong.service;

import javax.validation.Valid;

import java.util.List;

import com.alkemy.ong.dto.CategoryRequest;
import com.alkemy.ong.dto.CategoryResponse;

import com.alkemy.ong.dto.*;
import org.springframework.web.util.UriComponentsBuilder;


public interface CategoryService {
	
    CategoryResponse findCategoryById(Long id);
    
    String delete(Long id) throws Exception;

    String save(@Valid CategoryRequest categorytDto);

    List<CategoryResponse> findAllname();
    
    CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest);

    PageDto<CategoryResponse>getCategoriesByPage(int page, UriComponentsBuilder uriBuilder);
    
}
