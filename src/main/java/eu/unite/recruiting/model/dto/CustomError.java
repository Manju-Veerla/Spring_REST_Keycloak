package eu.unite.recruiting.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Represents a custom error response named {@link CustomError} structure for REST APIs.
 */
@Getter
@Builder
public class CustomError {

    private String header;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @Builder.Default
    private final Boolean isSuccess = false;

}
