package com.alkemy.ong.service.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alkemy.ong.dto.ActivityRequest;
import com.alkemy.ong.dto.ActivityResponse;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.model.Activity;
import com.alkemy.ong.repository.ActivityRepository;
import com.alkemy.ong.service.ActivityService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {
	
	private final MessageSource messageSource;
	private final ActivityRepository activityRepository;
	private final AmazonClient amazonClient;
	
	@Override
	@Transactional
	public ActivityResponse create(ActivityRequest activityRequest) {
		Activity activity = new Activity();
		activity.setName(activityRequest.getName());
		activity.setContent(activityRequest.getContent());
		activity.setImage(amazonClient.uploadFile(activityRequest.getImage()));
		activity = activityRepository.save(activity);
		return mapToDto(activity);
	}
	
	@Override
	@Transactional
	public ActivityResponse update(Long id, ActivityRequest activityRequest) {
		Activity activity = activityRepository.findById(id).orElseThrow(this::makeEntityNotFoundException);
		if (activityRequest.hasName())
			activity.setName(activityRequest.getName());
		if  (activityRequest.hasContent())
			activity.setContent(activityRequest.getContent());
		if (activityRequest.hasImage()) {
			amazonClient.deleteFileFromS3Bucket(activity.getImage());
			activity.setImage(amazonClient.uploadFile(activityRequest.getImage()));
		}
		return mapToDto(activity);
	}
	
	private ActivityResponse mapToDto(Activity activity) {
		return ActivityResponse.builder()
				.name(activity.getName())
				.content(activity.getContent())
				.image(activity.getImage())
				.build();
	}
	
	private EntityNotFoundException makeEntityNotFoundException() {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] {"Activity"}, Locale.US);
		return new EntityNotFoundException(error);
	}

}
