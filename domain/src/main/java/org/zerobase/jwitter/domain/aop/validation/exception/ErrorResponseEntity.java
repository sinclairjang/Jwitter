package org.zerobase.jwitter.domain.aop.validation.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Data
@Builder
public class ErrorResponseEntity {
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(MethodArgumentNotValidException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseEntity.builder()
                        .message(e.getMessage())
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(DomainConstraintException e) {

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .message(e.getMessage())
                        .build());
    }
}