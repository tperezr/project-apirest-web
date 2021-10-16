package com.alkemy.ong.controller;

import com.alkemy.ong.dto.SlideDTO;
import com.alkemy.ong.dto.SlideRequest;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.exception.ONGBadRequestException;
import com.alkemy.ong.service.ISlideService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Locale;

import javax.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Slides")
@RestController
@RequestMapping(path = "/slides")
@AllArgsConstructor
@Validated
public class SlideController {

    private ISlideService slideService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public ResponseEntity<List<SlideDTO>> getSlideList(){
        return ResponseEntity.ok(slideService.getSlideList());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getSlideById(@PathVariable("id") Long id){
        try{
            return new ResponseEntity<>(slideService.getSlideById(id), HttpStatus.OK);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String>create(
			@ModelAttribute SlideRequest slide){

    	if(slide == null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"Slide or Image"}, Locale.US));
		
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(slideService.insert(slide));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String>update(
			@PathVariable @Min(value = 1,message = "Id must be greater than or equal to 1")Long id,
			@ModelAttribute SlideRequest slide){

    	if(slide== null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"Slide or Image"}, Locale.US));
    			
		try {
			return ResponseEntity.status(HttpStatus.OK).body(slideService.update(slide, id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteSlide(@PathVariable("id") Long id){
        slideService.deleteSlide(id);
        String deletedMsg = messageSource.getMessage("entity.deleted", new Object[]{"Slide"}, Locale.US);
        return ResponseEntity.ok(deletedMsg + id);
    }

}
