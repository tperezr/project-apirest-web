package com.alkemy.ong.service;

import com.alkemy.ong.dto.SlideDTO;
import com.alkemy.ong.dto.SlideRequest;
import com.alkemy.ong.model.Slide;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.web.multipart.MultipartFile;

public interface ISlideService {
    List<SlideDTO> getSlideList();
    SlideDTO getSlideById(Long id);
	String insert(SlideRequest slide) throws Exception;
    void deleteSlide(Long id);
    String update(SlideRequest slideRequest, long id) throws Exception;

}
