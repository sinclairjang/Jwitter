package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class FollowSelfException extends RestException {
    public FollowSelfException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
