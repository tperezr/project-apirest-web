package com.alkemy.ong.dto;

import lombok.*;
@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlideDTO {
    private String imageUrl;
    private Integer slideOrder;
    private String text;
}
