package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class FollowSelfException extends RestException {
    public FollowSelfException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
