package com.emazon.ms_shopping_cart.infra.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {
    public static final String NOT_NULL = "must not be null";
    public static final String NOT_BLANK = "must not be blank";
    public static final String SIZE_BETWEEN_3_50 = "size must be between 3 and 50";
    public static final String SIZE_BETWEEN_3_90 = "size must be between 3 and 90";
    public static final String SIZE_BETWEEN_3_120 = "size must be between 3 and 120";
    public static final String UNIQUE_CONSTRAINT_VIOLATION = "must be unique";
    public static final String ENTITY_ALREADY_EXISTS = " already exists";
    public static final String FIELD_VALIDATION_ERRORS = "Request has field validation errors";
    public static final String ID_NOT_FOUND = "A provided ID could not be found";
    public static final String NOT_VALID_PARAM = "Not valid request param";
    public static final String MISSING_REQUIRED_PARAM = "Not valid request param";
    public static final String REQUIRED_PARAM = "Required param";
    public static final String EMAIL_NOT_FOUND = "Email could not be found.";
    public static final String INVALID_TOKEN = "Invalid token.";
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";
    public static final String BIRTH_DAY_CONSTRAINS = "Birth day doesn't meet the constrains.";

    public static final String USER_MUST_BE_OLDER = "User must be older.";
    public static final String EMAIL_MUST_BE_UNIQUE = "Email must be unique.";
    public static final String EMAIL_CONSTRAINS = "Email doesn't meet unique constrains.";

    private String message;
    private Map<String, Object> fieldErrors;
}
