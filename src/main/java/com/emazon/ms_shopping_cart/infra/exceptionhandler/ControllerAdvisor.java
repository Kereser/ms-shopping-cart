package com.emazon.ms_shopping_cart.infra.exceptionhandler;

import com.emazon.ms_shopping_cart.infra.exception.BaseEntityException;
import com.emazon.ms_shopping_cart.infra.exception.ResourceOwnershipViolationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    private final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestOnConstrains(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().message(ex.getMessage().split(":")[0]).build());
    }

    @ExceptionHandler(ResourceOwnershipViolationException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestOnConstrains(BaseEntityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().message(ex.getReason()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleFieldValidations(MethodArgumentNotValidException ex) {
        Map<String, Object> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));

        ExceptionResponse res = ExceptionResponse.builder()
                .message(ExceptionResponse.FIELD_VALIDATION_ERRORS)
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<ExceptionResponse> handleFeignBadRequest(FeignException.BadRequest ex) throws JsonProcessingException {
        ExceptionResponse res = mapper.readValue(ex.contentUTF8(), ExceptionResponse.class);
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(FeignException.Conflict.class)
    public ResponseEntity<ExceptionResponse> handleFeignBadRequest(FeignException.Conflict ex) throws JsonProcessingException {
        ExceptionResponse res = mapper.readValue(ex.contentUTF8(), ExceptionResponse.class);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    @ExceptionHandler(FeignException.Forbidden.class)
    public ResponseEntity<ExceptionResponse> handleFeignBadRequest(FeignException.Forbidden ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler({ConnectException.class, RetryableException.class})
    public ResponseEntity<ExceptionResponse> handleFeignBadRequest(Exception ex) {
        return ResponseEntity.internalServerError().body(ExceptionResponse.builder().message(ex.getMessage()).build());
    }
}
