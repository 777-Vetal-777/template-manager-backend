package com.itextpdf.dito.manager.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.license.EmptyLicenseFileException;
import com.itextpdf.dito.manager.exception.license.InvalidLicenseException;
import com.itextpdf.dito.manager.exception.license.UnreadableLicenseException;

@ControllerAdvice
public class LicenseExceptionHandler extends AbstractExceptionHandler {

	@ExceptionHandler(InvalidLicenseException.class)
	public ResponseEntity<ErrorResponseDTO> invalidLicenseExceptionHandler(final InvalidLicenseException ex) {
		return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmptyLicenseFileException.class)
	public ResponseEntity<ErrorResponseDTO> emptyLicenseFileExceptionHandler(final EmptyLicenseFileException ex) {
		return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnreadableLicenseException.class)
	public ResponseEntity<ErrorResponseDTO> unreadableLicenseExceptionHandler(final UnreadableLicenseException ex) {
		return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}
}
