package org.zerobase.jwitter.domain.aop.validation.exception;

import org.springframework.http.HttpStatus;

public class RedisInvalidCommandException extends RedisCustomException {
    public RedisInvalidCommandException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
