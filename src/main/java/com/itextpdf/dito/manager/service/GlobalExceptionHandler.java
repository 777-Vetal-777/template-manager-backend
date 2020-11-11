package com.itextpdf.dito.manager.service;

import com.itextpdf.dito.manager.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundErrorHandler(
            IllegalArgumentException ex) {
        log.error(ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse("Invalid request parameter",ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> emptyParamsValidationErrorHandler(
            MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        String errorMsg =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .findFirst()
                        .orElse(ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse("Validation error", errorMsg), HttpStatus.BAD_REQUEST);
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        String message;
        String details;
    }
}
