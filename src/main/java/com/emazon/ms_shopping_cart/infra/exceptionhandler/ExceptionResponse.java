package com.emazon.ms_shopping_cart.infra.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    public static final String NOT_NULL = "must not be null";
    public static final String FIELD_VALIDATION_ERRORS = "Request has field validation errors";
    public static final String ID_NOT_FOUND = "A provided ID could not be found";
    public static final String NOT_VALID_PARAM = "Not valid request param";
    public static final String INVALID_TOKEN = "Invalid token.";

    public static final String ERROR_PROCESSING_OPERATION = "Error processing operation with: ";
    public static final String PURCHASE_FAILED_MSG = "An error occurred while trying to process purchase for userId: %s";

    private String message;
    private Map<String, Object> fieldErrors;
}
