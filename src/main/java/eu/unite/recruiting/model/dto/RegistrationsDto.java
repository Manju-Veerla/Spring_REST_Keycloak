package eu.unite.recruiting.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationsDto {

    private Integer id;

    @NotBlank(message = "Code cannot be empty")
    @Size(min = 5, max = 15, message = "Code of workshop must be of size 5-15")
    private String workshopCode;

    private String userName;

    private String userEmail;

    private String userPhone;

    private String userPreferredContact;

}
