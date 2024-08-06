package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class TokenTypeException extends RestException {
    public TokenTypeException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
