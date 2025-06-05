package com.app.model.response;

import com.app.model.entity.PreferredContact;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationsResponse {

    private Integer registrationId;

    private String workshopCode;

    private String userName;

    private String userEmail;

    private String userPhone;

    private PreferredContact userPreferredContact;

}
