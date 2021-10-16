package com.alkemy.ong.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import com.alkemy.ong.dto.NewsCommentsRes;
import com.alkemy.ong.dto.NewsRequest;
import com.alkemy.ong.dto.NewsResponse;

public interface NewsService {

	public String insert(NewsRequest news) throws Exception;
	public String delete(Long id) throws Exception;
	public NewsResponse findNewsById(Long id);
	public ResponseEntity<?> updateById(NewsRequest news, Long id);
    public List<NewsCommentsRes> getComments(Long id);
	Page<NewsResponse> findAll(Pageable pageReqest);
	
}
