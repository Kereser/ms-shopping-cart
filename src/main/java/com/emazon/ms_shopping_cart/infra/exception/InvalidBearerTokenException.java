package com.emazon.ms_shopping_cart.infra.exception;

import com.emazon.ms_shopping_cart.infra.exceptionhandler.ExceptionResponse;
import org.springframework.security.core.AuthenticationException;

public class InvalidBearerTokenException extends AuthenticationException {
    public InvalidBearerTokenException() {
        super(ExceptionResponse.INVALID_TOKEN);
    }
}
