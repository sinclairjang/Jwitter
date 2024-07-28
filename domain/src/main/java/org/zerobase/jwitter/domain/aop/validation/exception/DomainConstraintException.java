package org.zerobase.jwitter.domain.aop.validation.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class DomainConstraintException extends RuntimeException {
    private final HttpStatus httpStatus;

    public DomainConstraintException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
