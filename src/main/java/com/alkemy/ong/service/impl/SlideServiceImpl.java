package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.SlideDTO;
import com.alkemy.ong.dto.SlideRequest;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.model.Organization;
import com.alkemy.ong.model.Slide;
import com.alkemy.ong.repository.OrganizationRepository;
import com.alkemy.ong.repository.SlideRepository;
import com.alkemy.ong.service.ISlideService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SlideServiceImpl implements ISlideService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SlideRepository slideRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
	@Autowired
	private AmazonClient amazonClient;
    
    @Override
    public List<SlideDTO> getSlideList() {
        List<Slide> slide = slideRepository.findAllByOrderBySlideOrderAsc();
        if (slide.isEmpty()) {
            String notFoundMsg = messageSource.getMessage("no.records.found",new Object[]{"Slides"}, Locale.US);
            throw new ResponseStatusException(HttpStatus.OK, notFoundMsg);
        }
        List<SlideDTO> mappedSlide = slide
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return mappedSlide;
    }

    @Override
    public SlideDTO getSlideById(Long id) {
        Optional<Slide> slide = slideRepository.findById(id);
        if (slide.isPresent()){
            return this.mapToDTO(slide.get());
        }
        else {
            String notFoundMsg = messageSource.getMessage("error.entity.not-found",new Object[]{"Slides"}, Locale.US);
            throw new EntityNotFoundException(notFoundMsg);
        }
    }

	@Override
	public String insert(SlideRequest slide) throws Exception {
		String message = "";
		Optional<Organization> organizations = organizationRepository.findAll().stream().findFirst();
		Organization organization =  organizations.orElse(null);

		if(slide.getSlideOrder() == 0) {
			Integer newSlideOrder = (int) (slideRepository.count() + 1);
			slide.setSlideOrder(newSlideOrder);
		}
		
		try {
			String urlImage = amazonClient.uploadFile(slide.getImage());
			slideRepository.save(new Slide(urlImage, slide.getText(),slide.getSlideOrder(),organization));
			message = messageSource.getMessage("entity.created", new Object[]{"Slide"}, Locale.US);
		} catch (Exception e) {
			message = messageSource.getMessage("entity.created.error", new Object[]{"Slide"}, Locale.US);
			throw new Exception(message);
		}
		
		return message;
	}
    
    @Override
    public void deleteSlide(Long id) {
        Optional<Slide> slide = slideRepository.findById(id);
        if (!slide.isPresent()){
            String notFoundMsg = messageSource.getMessage("error.entity.not-found",new Object[]{"Slide"}, Locale.US);
            throw new EntityNotFoundException(notFoundMsg);
        }
        else {
            slideRepository.deleteById(id);
        }
    }

	@Override
	public String update(SlideRequest slideRequest,long id) throws Exception {
		String message = "";
		
		Slide slide = slideRepository.findById(id).orElse(null);
		boolean updateSomething = false;

		try {
			String urlImage = "";
			
			if(slideRequest.getImage() != null)
				urlImage = amazonClient.uploadFile(slideRequest.getImage());
			
			if(!urlImage.isBlank() && !urlImage.equalsIgnoreCase(slide.getImageUrl())) {
				amazonClient.deleteFileFromS3Bucket(slide.getImageUrl());
				slide.setImageUrl(urlImage);
				updateSomething = true;
			}

			if(slideRequest != null) {
				if(slideRequest.hasText()) {
					slide.setText(slideRequest.getText());
					updateSomething = true;
				}
				if((slideRequest.hasSlideOrder())) {
					slide.setSlideOrder(slideRequest.getSlideOrder());
					updateSomething = true;
				}
			}
			
			slideRepository.save(slide);
			
			if(updateSomething)
				message = messageSource.getMessage("entity.update", new Object[]{"Slide"}, Locale.US);
			else
				message = messageSource.getMessage("entity.update.empty", new Object[]{"Slide"}, Locale.US);
			
		} 
		catch (Exception e) {
			int errorString = e.getLocalizedMessage().indexOf(';');
			message = messageSource.getMessage("entity.update.error", new Object[] {"Slide", 
					e.getLocalizedMessage().substring(0, errorString)}, Locale.US);
			throw new Exception(message);
		}
		
		return message;
	}
	
	private SlideDTO mapToDTO(Slide slide) {
		return SlideDTO.builder()
				.imageUrl(slide.getImageUrl())
				.text(slide.getText())
				.slideOrder(slide.getSlideOrder())
				.build();
	}

}
