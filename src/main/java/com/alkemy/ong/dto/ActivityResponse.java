package com.alkemy.ong.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ActivityResponse {

    private String name;
    private String content;
    private String image;
    
}
