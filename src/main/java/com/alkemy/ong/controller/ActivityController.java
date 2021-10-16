package com.alkemy.ong.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alkemy.ong.dto.ActivityRequest;
import com.alkemy.ong.dto.ActivityResponse;
import com.alkemy.ong.exception.ONGBadRequestException;
import com.alkemy.ong.service.ActivityService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Activities")
@RestController
@RequestMapping("/activities")
@AllArgsConstructor
public class ActivityController {
	
	private final MessageSource messageSource;
	private final ActivityService activityService;
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ActivityResponse> createActivity(
			@ModelAttribute ActivityRequest activityRequest){	
		if (activityRequest == null)
			throwMissingDtoException();

		return ResponseEntity.status(HttpStatus.CREATED).body(activityService.create(activityRequest));
	}
	
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityResponse> updateActivity(@PathVariable Long id, 
    		@ModelAttribute ActivityRequest activityRequest){	
		
		if (activityRequest == null)
			throwMissingDtoException();
		
    	return ResponseEntity.status(HttpStatus.OK).body(activityService.update(id, activityRequest));
    }
	
	private void throwMissingDtoException() {
		String message = messageSource.getMessage("error.controller.missing-dto", new Object[] {"Activity"}, Locale.US);
		throw new ONGBadRequestException(message);
	}

}
