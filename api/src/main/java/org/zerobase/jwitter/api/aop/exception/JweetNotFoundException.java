package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class JweetNotFoundException extends RestException {
    public JweetNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
