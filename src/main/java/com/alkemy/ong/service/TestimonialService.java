package com.alkemy.ong.service;

import com.alkemy.ong.dto.PageDto;
import com.alkemy.ong.dto.TestimonialRequest;
import com.alkemy.ong.dto.TestimonialResponse;
import com.alkemy.ong.model.Testimonials;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

public interface TestimonialService {
	public String insert(TestimonialRequest testimonial) throws Exception;
	public Testimonials findTestimonialsById(Long id);
	public ResponseEntity<?> updateById(TestimonialRequest testimonial, Long id);
	Boolean deleteTestimonialById(Long id);
	PageDto<TestimonialResponse> getTestimonialsByPage(int page, UriComponentsBuilder uriBuilder);
}
