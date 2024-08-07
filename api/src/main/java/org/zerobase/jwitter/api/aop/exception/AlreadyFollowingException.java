package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class AlreadyFollowingException extends RestException {
    public AlreadyFollowingException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
