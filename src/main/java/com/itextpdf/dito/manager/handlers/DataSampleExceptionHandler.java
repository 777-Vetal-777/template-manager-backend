package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleStructureException;
import com.itextpdf.dito.manager.exception.resource.ResourceFileSizeExceedLimitException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DataSampleExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(ResourceFileSizeExceedLimitException.class)
    public ResponseEntity<ErrorResponseDTO> invalidDataSampleExceptionHandler(final InvalidDataSampleException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceFileSizeExceedLimitException.class)
    public ResponseEntity<ErrorResponseDTO> invalidDataSampleStructureExceptionHandler(
            final InvalidDataSampleStructureException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
