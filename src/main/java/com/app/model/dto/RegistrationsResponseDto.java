package com.app.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationsResponseDto {

    private Integer id;

    private String workshopCode;

    private String userPhone;

    private String userPreferredContact;

}
