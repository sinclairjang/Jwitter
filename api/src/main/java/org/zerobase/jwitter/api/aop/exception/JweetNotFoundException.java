package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class JweetNotFoundException extends RestException {
    public JweetNotFoundException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
