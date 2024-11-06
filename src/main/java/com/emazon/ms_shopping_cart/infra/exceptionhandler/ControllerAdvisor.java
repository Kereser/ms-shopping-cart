package com.emazon.ms_shopping_cart.infra.exceptionhandler;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.infra.exception.BaseEntityException;
import com.emazon.ms_shopping_cart.infra.exception.NoDataFoundException;
import com.emazon.ms_shopping_cart.infra.exception.PurchaseFailedException;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    private final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestOnConstrains(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().message(ex.getMessage().split(ConsUtils.COLON_DELIMITER)[0]).build());
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

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoDataFound(BaseEntityException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put(ex.getField(), ExceptionResponse.ID_NOT_FOUND);

        ExceptionResponse res = ExceptionResponse.builder()
                .message(ExceptionResponse.ERROR_PROCESSING_OPERATION + ex.getEntityName())
                .fieldErrors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(PurchaseFailedException.class)
    public ResponseEntity<ExceptionResponse> handlePurchaseExcepiton(BaseEntityException ex) {
        ExceptionResponse res = ExceptionResponse.builder()
                .message(String.format(ExceptionResponse.PURCHASE_FAILED_MSG, ex.getReason()))
                .build();

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(res);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleNotValidReqParam(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.builder()
                .message(ExceptionResponse.NOT_VALID_PARAM)
                .fieldErrors(Map.of(ex.getName(), ex.getValue() != null ? ex.getValue() : ConsUtils.EMPTY)).build());
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

    @ExceptionHandler(FeignException.InternalServerError.class)
    public ResponseEntity<ExceptionResponse> handleFeignInternalError(FeignException.InternalServerError ex) throws JsonProcessingException {
        ExceptionResponse res = mapper.readValue(ex.contentUTF8(), ExceptionResponse.class);
        return ResponseEntity.internalServerError().body(res);
    }

    @ExceptionHandler({ConnectException.class, RetryableException.class})
    public ResponseEntity<ExceptionResponse> handleFeignBadRequest(Exception ex) {
        return ResponseEntity.internalServerError().body(ExceptionResponse.builder().message(ex.getMessage()).build());
    }
}
