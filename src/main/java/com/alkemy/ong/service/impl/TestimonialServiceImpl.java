package com.alkemy.ong.service.impl;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alkemy.ong.dto.PageDto;
import com.alkemy.ong.dto.TestimonialRequest;
import com.alkemy.ong.dto.TestimonialResponse;
import com.alkemy.ong.model.Testimonials;
import com.alkemy.ong.repository.TestimonialRepository;
import com.alkemy.ong.service.TestimonialService;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TestimonialServiceImpl implements TestimonialService{

	private static final int PAGE_SIZE = 10;
	@Autowired
	TestimonialRepository testimonialRepository;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private AmazonClient amazonClient;
	
	@Override
	public String insert(TestimonialRequest testimonial) throws Exception {
		String message = "";

		try {
			String urlImage = amazonClient.uploadFile(testimonial.getImage());
			testimonialRepository.save(
					new Testimonials(testimonial.getName(), testimonial.getContent(),
							urlImage)
					);
			message = messageSource.getMessage("entity.created", new Object[]{"Testimonial"}, Locale.US);
		} catch (Exception e) {
			message = messageSource.getMessage("entity.created.error", new Object[]{"Testimonial"}, Locale.US);
			throw new Exception(message);
		}
		return message;
	}

	@Override
	public Testimonials findTestimonialsById(Long id) {
		Optional<Testimonials> testimonial = testimonialRepository.findById(id);
		return testimonial.orElse(null);
	}

	@Override
	public ResponseEntity<?> updateById(TestimonialRequest updateTestimonial, Long id) {

		Optional<Testimonials> oldTestimonials = testimonialRepository.findById(id);

		if (oldTestimonials.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage("testimonials.error.notFound", new Object[]{"testimonials"}, Locale.US));
		}
		try {

			if (updateTestimonial.getImage() != null) {
				amazonClient.deleteFileFromS3Bucket(oldTestimonials.get().getImage());
				String urlImage = amazonClient.uploadFile(updateTestimonial.getImage());
				oldTestimonials.get().setImage(urlImage);
			}
			if (updateTestimonial.getName() != null){
				oldTestimonials.get().setName(updateTestimonial.getName());
			}

			if (updateTestimonial.getContent() != null){
				oldTestimonials.get().setContent(updateTestimonial.getContent());
			}


			testimonialRepository.save(oldTestimonials.get());

			return ResponseEntity.ok(oldTestimonials.get());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource.getMessage("testimonials.error.cantUpdate", new Object[]{"testimonials"}, Locale.US));
		}

	}

	@Override
	public Boolean deleteTestimonialById(Long id) {
		Testimonials testimonial = this.findTestimonialsById(id);
		if(testimonial != null){
			testimonialRepository.deleteById(id);
			return true;
		}
		return false;
	}
	
	@Override
	public PageDto<TestimonialResponse> getTestimonialsByPage(int page, UriComponentsBuilder uriBuilder) {
		Pageable pageable = PageRequest.of(page,PAGE_SIZE);
		Page<TestimonialResponse> testimonialsPage = testimonialRepository.findAll(pageable).map(this::mapToDto);
		PageDto<TestimonialResponse> pageDto = new PageDto<>();

		if(!(page == 0)){
			pageDto.getLinks().put("prev",buildUrlPage(page-1,uriBuilder));
		}
		if(!(page == testimonialsPage.getTotalPages()-1)){
			pageDto.getLinks().put("next",buildUrlPage(page+1,uriBuilder));
		}
		pageDto.setContent(testimonialsPage.getContent());
		return pageDto;
	}

	public String buildUrlPage(int page,UriComponentsBuilder uriBuilder){
		return uriBuilder.toUriString() + "/testimonials?page=" + page;
	}
	
	private TestimonialResponse mapToDto(Testimonials testimonials) {
		return TestimonialResponse.builder()
				.name(testimonials.getName())
				.image(testimonials.getImage())
				.content(testimonials.getContent())
				.build();
	}
}
