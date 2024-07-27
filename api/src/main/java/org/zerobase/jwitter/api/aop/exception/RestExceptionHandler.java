package org.zerobase.jwitter.api.aop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorResponseEntity> handleApplicationException(RestException e) {
        log.error(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(e);
    }

}
