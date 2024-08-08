package org.zerobase.jwitter.api.aop.exception.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.zerobase.jwitter.api.aop.exception.root.DomainConstraintException;
import org.zerobase.jwitter.api.aop.exception.root.RestException;

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

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(RestException e) {

        return ResponseEntity
                .status(e.getHttpStatus())
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