package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;
import org.zerobase.jwitter.api.aop.exception.root.RestException;

public class UserAlreadyExistsException extends RestException {
    public UserAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
