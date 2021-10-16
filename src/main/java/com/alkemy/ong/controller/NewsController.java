package com.alkemy.ong.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alkemy.ong.dto.NewsCommentsRes;
import com.alkemy.ong.dto.NewsRequest;
import com.alkemy.ong.dto.NewsResponse;
import com.alkemy.ong.dto.PageDto;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.exception.ONGBadRequestException;
import com.alkemy.ong.service.NewsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "News")
@RestController
@RequestMapping("/news")
@Validated
public class NewsController {

    @Autowired
    private NewsService newsService;
	@Autowired
	private MessageSource messageSource;

	@Operation(summary = "Get details news", description = "Returns a news in detail by id for an Admin")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "News found"),
			@ApiResponse(responseCode = "404", description = "News not found with the id"),
			@ApiResponse(responseCode = "403", description = "Don't have admin permissions"),
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body.")
	})
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getNewsDetailsById(
            @PathVariable @Min(value = 1,message = "Id must be greater than or equal to 1")Long id) throws EntityNotFoundException {
        NewsResponse news = newsService.findNewsById(id);
        if(news == null){
			String mssg = messageSource.getMessage("error.entity.not-found-id",new Object[]{"News",id}, Locale.US);
			throw new EntityNotFoundException(mssg);
        }
        return ResponseEntity.ok(news);
    }

	@Operation(summary = "Create news", description = "Returns if the news was created or not.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "News created successfully."),
			@ApiResponse(responseCode = "409", description = "Error. News could not be created."),
			@ApiResponse(responseCode = "403", description = "Don't have admin permissions"),
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body.")
	})
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String>create(@ModelAttribute NewsRequest news){
		if(news == null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"News or Image"}, Locale.US));

		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(newsService.insert(news));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@Operation(summary = "Update news", description = "Returns if the news was updated or not.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "News updated successfully."),
			@ApiResponse(responseCode = "409", description = "Error. News could not be updated."),
			@ApiResponse(responseCode = "403", description = "Error. Don't have admin permissions"),
			@ApiResponse(responseCode = "404", description = "Error. News was not found"),
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body.")
	})
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?>updateNews(@ModelAttribute NewsRequest updateNews, @PathVariable("id") Long id){
    	if(updateNews == null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"News or Image"}, Locale.US));
    	
		return newsService.updateById(updateNews, id);
	}

	@Operation(summary = "Delete news", description = "Returns if the news was deleted or not.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "News deleted successfully."),
			@ApiResponse(responseCode = "403", description = "Error. Don't have admin permissions"),
			@ApiResponse(responseCode = "404", description = "Error. News was not found"),
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body.")
	})
	@DeleteMapping("/{id:[0-9]{1,40}}") 
	public ResponseEntity<String>delete(@PathVariable(value="id", required=true) Long id){
		try {
			return ResponseEntity.status(HttpStatus.OK).body(newsService.delete(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@Operation(summary = "Get news", description = "Returns 10 news for page.")
	@GetMapping
	public ResponseEntity<?> getPagedNews(@ParameterObject @PageableDefault(size = 10) Pageable pageRequest, HttpServletRequest request) {
		Page<NewsResponse> page = newsService.findAll(pageRequest);
		List<NewsResponse> content = page.getContent();
		if (content.isEmpty()) {
			String message = messageSource.getMessage("no.records.found", new Object[]{"News"}, Locale.US);
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		PageDto<NewsResponse> response = new PageDto<>();
		response.setContent(content);
		int pageNumber = page.getNumber();
		Map<String, String> links = new HashMap<>();
		if (!page.isFirst())
			links.put("prev", makePaginationLink(request, pageNumber - 1));
		if (!page.isLast())
			links.put("next", makePaginationLink(request, pageNumber + 1));
		response.setLinks(links);		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	private String makePaginationLink(HttpServletRequest request, int page) {
		return String.format("%s?page=%d", request.getRequestURI(), page);
	}

	@Operation(summary = "Get news comments", description = "Returns news comments or not.")
	@GetMapping("/{id:[0-9]{1,40}}/comments")
	public ResponseEntity<?> getComments(@PathVariable(value="id", required=true) Long id){

		List<NewsCommentsRes> comments = newsService.getComments(id);
		if(comments.isEmpty()){
			return ResponseEntity.status(HttpStatus.OK).body(messageSource.getMessage("news.error.nocomments", null, Locale.US));
		}else{
			return ResponseEntity.status(HttpStatus.OK).body(comments);
		}
	}
}
