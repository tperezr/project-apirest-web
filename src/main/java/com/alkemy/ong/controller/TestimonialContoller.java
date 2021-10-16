package com.alkemy.ong.controller;

import com.alkemy.ong.exception.ONGBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.alkemy.ong.dto.PageDto;
import com.alkemy.ong.dto.TestimonialRequest;
import com.alkemy.ong.dto.TestimonialResponse;
import com.alkemy.ong.service.TestimonialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.constraints.Min;
import java.util.Locale;

@Tag(name = "Testimonials")
@RestController
@RequestMapping("/testimonials")
@Validated
public class TestimonialContoller {

    @Autowired
    private TestimonialService testimonialService;
	@Autowired
	private MessageSource messageSource;
    

	@Operation(summary="Creates a new testimonial", description = "Returns if the testimonial was created or not.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Testimonial Successfully created."), 
			@ApiResponse(responseCode = "409", description = "Error. The testimonial coul not be added."), 
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body")})
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String>create(@ModelAttribute TestimonialRequest testimonial){

		if(testimonial.getName() == null || testimonial.getContent() == null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"Testimonial or Image"}, Locale.US));
		
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(testimonialService.insert(testimonial));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@Operation(summary="Updates a testimonial by the given id", description = "Returns if the testimonial was updated or not.")
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Testimonial Successfully updated."), 
			@ApiResponse(responseCode = "409", description = "Error. The testimonial coul not be updated."), 
			@ApiResponse(responseCode = "404", description = "Error. The testimonial was not found."),
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body")})
	public ResponseEntity<?>updateTestimonials(@ModelAttribute TestimonialRequest updateTestimonials, @PathVariable("id") Long id){

		if(updateTestimonials == null)
			throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"Testimonial or Image"}, Locale.US));

		return	testimonialService.updateById(updateTestimonials,id);

	}
	
	@Operation(summary="Deletes a testimonial by the give id", description = "Returns if the testimonial could be deleted or not.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Testimonial Successfully created."), 
			@ApiResponse(responseCode = "404", description = "Error. The testimonial was not found."), 
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body")})
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTestimonial(
			@PathVariable @Min(value = 1,message = "Id must be greater than or equal to 1") Long id){
		if(testimonialService.deleteTestimonialById(id)){
			return ResponseEntity.ok(messageSource.getMessage("entity.deleted",new Object[]{"Testimonial"},Locale.US));
		}
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(messageSource.getMessage("error.entity.not-found-id", new Object[]{"Testimonial",id},Locale.US));
	}
	
	@Operation(summary="Get testimonials", description = "Returns 10 testimonials for page.")
    @GetMapping
    public ResponseEntity<?> getTestimonialsByPage(@RequestParam(name="page", required=false) Integer page, UriComponentsBuilder uriComponentsBuilder){
    	if(page == null) page = 0;
        PageDto<TestimonialResponse> pageDto = testimonialService.getTestimonialsByPage(page, uriComponentsBuilder);
        if(pageDto.getContent().isEmpty()){
            String message = messageSource.getMessage("error.entity.empty",new Object[]{"Testimonials"},Locale.US);
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.ok(pageDto);
    }
}
