package org.zerobase.jwitter.domain.aop.validation.exception;

import org.springframework.http.HttpStatus;

public class RedisKeyNotExistException extends RedisCustomException {
    public RedisKeyNotExistException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
