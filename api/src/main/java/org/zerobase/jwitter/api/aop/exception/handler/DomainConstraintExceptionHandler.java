package org.zerobase.jwitter.api.aop.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zerobase.jwitter.api.aop.exception.dto.ErrorResponseEntity;
import org.zerobase.jwitter.api.aop.exception.root.DomainConstraintException;

@Slf4j
@RestControllerAdvice
public class DomainConstraintExceptionHandler {

    @ExceptionHandler(DomainConstraintException.class)
    public ResponseEntity<ErrorResponseEntity> handleApplicationException(DomainConstraintException e) {
        log.error(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(e);
    }

}
