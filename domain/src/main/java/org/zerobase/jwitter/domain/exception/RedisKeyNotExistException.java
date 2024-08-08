package org.zerobase.jwitter.domain.exception;

import org.springframework.http.HttpStatus;

public class RedisKeyNotExistException extends RedisCustomException {
    public RedisKeyNotExistException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
