package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class JweetCommentNotFoundException extends RestException {
    public JweetCommentNotFoundException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
