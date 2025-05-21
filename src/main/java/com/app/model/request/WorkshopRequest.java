package com.app.model.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Builder
public class WorkshopRequest {

    @NotBlank(message = "Code cannot be empty")
    @Size(min = 5, max = 15, message = "Code of workshop must be of size 5-15")
    private String code;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 5, max = 50, message = "Name of workshop must be of size 5-15")
    private String name;

    @NotBlank (message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Start time cannot be empty")
    @FutureOrPresent(message = "Start time must be in the future or present")
    private ZonedDateTime startTime;

    @NotNull(message = "End time cannot be empty")
    @FutureOrPresent(message = "End time must be in the future or present")
    private ZonedDateTime endTime;

    @NotNull(message = "Capacity cannot be empty")
    @Min(value = 1, message = "Capacity must be greater than 0")
    private Integer capacity;
}
