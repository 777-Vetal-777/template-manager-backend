package com.itextpdf.dito.manager.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.integration.InconsistencyException;

@ControllerAdvice
public class IntegrationExceptionHandler extends AbstractExceptionHandler {
	@ExceptionHandler(InconsistencyException.class)
	public ResponseEntity<ErrorResponseDTO> inconsistencyExceptionHandler(final InconsistencyException ex) {
		return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.CONFLICT);
	}
}
