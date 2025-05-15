package eu.unite.recruiting.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class WorkshopUpdateDto {



    @Size(min = 5, max = 50, message = "Name of workshop must be of size 5-15")
    private String name;


    private String description;


    @FutureOrPresent
    private ZonedDateTime startTime;


    @FutureOrPresent
    private ZonedDateTime endTime;


    @Min(value = 1, message = "Capacity must be greater than 0")
    private Integer capacity;
}
