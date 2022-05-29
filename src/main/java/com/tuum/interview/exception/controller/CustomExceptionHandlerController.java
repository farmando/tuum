package com.tuum.interview.exception.controller;

import com.tuum.interview.exception.response.ExceptionResponse;
import com.tuum.interview.exception.response.InvalidRequestException;
import com.tuum.interview.exception.response.ResourceNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RestController
public class CustomExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        String message = ex.getMessage();

        ExceptionResponse exceptionResponse = null;

        if (message.contains("do_not_allow_transaction_when_insuficient_balance")) {
            exceptionResponse = new ExceptionResponse("Insufficient balance.", request.getDescription(false));
        } else {
            exceptionResponse = new ExceptionResponse("Unknow error. " + ex.getMessage(), request.getDescription(false));
        }

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<Object> databaseConflict(DataIntegrityViolationException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public final ResponseEntity<Object> invalidCurrencyRequest(Exception ex, WebRequest request) {
        ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
            , HttpHeaders headers
            , HttpStatus status
            , WebRequest request) {

        AtomicInteger errorIndex = new AtomicInteger(0);;
        StringBuilder errorMessage = new StringBuilder();
        ex.getAllErrors().forEach(e -> errorMessage.append(String.format("%d - %s", errorIndex.incrementAndGet(), e.getDefaultMessage())));
        ExceptionResponse exceptionResponse = new ExceptionResponse("Validation failed", errorMessage.toString());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,HttpStatus status, WebRequest request) {

        ExceptionResponse exceptionResponse = new ExceptionResponse("Validation failed", ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
