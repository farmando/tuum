package com.tuum.interview.exception.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException() {
        super("Invalid currency");
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}
