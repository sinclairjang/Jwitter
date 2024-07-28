package org.zerobase.jwitter.domain.aop.validation.exception;

import org.springframework.http.HttpStatus;

public class FollowSelfException extends DomainConstraintException {
    public FollowSelfException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
