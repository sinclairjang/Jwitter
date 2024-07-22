package org.zerobase.jwitter.api.aop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
//@ControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseEntity> handleValidationException(MethodArgumentNotValidException e)
            throws MethodArgumentNotValidException {

        log.info(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(e);
    }
}
