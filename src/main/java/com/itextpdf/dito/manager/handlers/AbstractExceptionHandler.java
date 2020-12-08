package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractExceptionHandler {
    private static final Logger log = LogManager.getLogger(AbstractExceptionHandler.class);

    protected ResponseEntity<ErrorResponseDTO> buildErrorResponse(final Throwable throwable,
            final HttpStatus httpStatus) {
        log.error(throwable.getMessage(), throwable);
        return new ResponseEntity<>(buildErrorResponseDTO(throwable.getMessage()), httpStatus);
    }

    protected ResponseEntity<ErrorResponseDTO> buildErrorResponse(final Throwable throwable, final String message,
            final HttpStatus httpStatus) {
        log.error(throwable.getMessage(), throwable);
        return new ResponseEntity<>(buildErrorResponseDTO(message), httpStatus);
    }

    private ErrorResponseDTO buildErrorResponseDTO(final String message) {
        return new ErrorResponseDTO(message);
    }
}
