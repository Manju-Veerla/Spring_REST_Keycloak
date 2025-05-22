package com.app.model.request;

import com.app.model.entity.PreferredContact;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationsRequest {

    private Integer registrationId;

    @NotBlank(message = "Code cannot be empty")
    @Size(min = 5, max = 15, message = "Code of workshop must be of size 5-15")
    private String workshopCode;

    private String userName;

    private String userEmail;

    private String userPhone;

    @NotNull(message = "Preferred contact method is required")
    private PreferredContact userPreferredContact;

}
