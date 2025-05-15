package eu.unite.recruiting.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationsResponseDto {

    private Integer id;

    private String workshopCode;

    private String userPhone;

    private String userPreferredContact ;

}
