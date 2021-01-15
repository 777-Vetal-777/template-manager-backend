package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionFileSizeExceedLimitException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionHasDependenciesException;
import com.itextpdf.dito.manager.exception.datacollection.EmptyDataCollectionFileException;
import com.itextpdf.dito.manager.exception.datacollection.InvalidDataCollectionException;
import com.itextpdf.dito.manager.exception.datacollection.UnreadableDataCollectionException;

import com.itextpdf.dito.manager.exception.resource.ResourceFileSizeExceedLimitException;
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

    @ExceptionHandler(EmptyDataCollectionFileException.class)
    public ResponseEntity<ErrorResponseDTO> fileUploadExceptionHandler(final EmptyDataCollectionFileException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataCollectionHasDependenciesException.class)
    public ResponseEntity<ErrorResponseDTO> dataCollectionHasDependenciesExceptionHandler(final DataCollectionHasDependenciesException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataCollectionFileSizeExceedLimitException.class)
    public ResponseEntity<ErrorResponseDTO> dataCollectionFileSizeExceedLimitExceptionHandler(
            final DataCollectionFileSizeExceedLimitException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
