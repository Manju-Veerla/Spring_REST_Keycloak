package com.app.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a custom error response named {@link CustomErrorResponse} structure for REST APIs.
 */
@Getter
@Builder
public class CustomErrorResponse {

    private String header;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @Builder.Default
    private final Boolean isSuccess = false;

}
