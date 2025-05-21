package com.app.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@Builder
public class WorkshopResponse {

    private String code;
    private String name;
    private String description;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Integer capacity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<RegistrationsResponse> registrations;
}
