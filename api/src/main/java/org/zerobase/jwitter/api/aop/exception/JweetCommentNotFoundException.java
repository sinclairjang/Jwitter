package org.zerobase.jwitter.api.aop.exception;

import org.springframework.http.HttpStatus;

public class JweetCommentNotFoundException extends RestException {
    public JweetCommentNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
