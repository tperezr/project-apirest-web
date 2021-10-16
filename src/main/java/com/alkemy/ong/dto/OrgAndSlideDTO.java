package com.alkemy.ong.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgAndSlideDTO {
    List<OrganizationResponse> organizationResponse = new ArrayList<>();
    List<SlideDTO> slideDTO = new ArrayList<>();
}
