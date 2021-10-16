package com.alkemy.ong.service;

import com.alkemy.ong.dto.ActivityRequest;
import com.alkemy.ong.dto.ActivityResponse;

public interface ActivityService {

	ActivityResponse create(ActivityRequest activityRequest);
	ActivityResponse update(Long id, ActivityRequest activityRequest);
	
}
