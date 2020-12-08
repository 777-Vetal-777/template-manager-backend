package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.datacollection.InvalidDataCollectionException;
import com.itextpdf.dito.manager.exception.datacollection.UnreadableDataCollectionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DataCollectionExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> invalidDataCollectionExceptionHandler(
            final InvalidDataCollectionException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> unreadableDataCollectionExceptionHandler(
            final UnreadableDataCollectionException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
