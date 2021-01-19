package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.datasample.DataSampleNameAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleStructureException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DataSampleExceptionHandler extends AbstractExceptionHandler {
	
    @ExceptionHandler(InvalidDataSampleException.class)
    public ResponseEntity<ErrorResponseDTO> invalidDataSampleExceptionHandler(final InvalidDataSampleException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataSampleStructureException.class)
    public ResponseEntity<ErrorResponseDTO> invalidDataSampleStructureExceptionHandler(
            final InvalidDataSampleStructureException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
}
