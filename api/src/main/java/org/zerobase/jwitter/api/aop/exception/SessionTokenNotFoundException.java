package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;
import org.zerobase.jwitter.api.aop.exception.root.RestException;

public class SessionTokenNotFoundException extends RestException {
    public SessionTokenNotFoundException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
