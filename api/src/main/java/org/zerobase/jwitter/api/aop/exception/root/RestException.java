package org.zerobase.jwitter.api.aop.exception.root;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RestException extends RuntimeException {
    private final HttpStatus httpStatus;

    public RestException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
