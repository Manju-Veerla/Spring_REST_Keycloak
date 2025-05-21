package com.app.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationsResponse {

    private Integer registrationId;

    private String workshopCode;

    private String userPhone;

    private String userPreferredContact;

}
