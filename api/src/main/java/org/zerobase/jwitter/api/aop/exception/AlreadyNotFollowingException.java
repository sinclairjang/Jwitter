package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;
import org.zerobase.jwitter.api.aop.exception.root.RestException;

public class AlreadyNotFollowingException extends RestException {
    public AlreadyNotFollowingException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
