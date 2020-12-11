package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.mail.DailyMailQuotaExceededException;
import com.itextpdf.dito.manager.exception.sort.UnsupportedSortFieldException;

import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {
    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AbstractResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> resourceNotFoundExceptionHandler(
            final AbstractResourceNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AbstractResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> resourceAlreadyExistsExceptionHandler(final AbstractResourceAlreadyExistsException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> methodArgumentNotValidExceptionHandler(
            final MethodArgumentNotValidException ex) {
        final String fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new StringBuilder()
                        .append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .toString())
                .collect(Collectors.joining("\n"));
        final String message = new StringBuilder("Validation error: ").append(fieldErrors).toString();
        return buildErrorResponse(ex, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedSortFieldException.class)
    public ResponseEntity<ErrorResponseDTO> unsupportedSortFieldExceptionHandler(final UnsupportedSortFieldException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DailyMailQuotaExceededException.class)
    public ResponseEntity<ErrorResponseDTO> dailyMailQuotaExceededExceptionHandler(final DailyMailQuotaExceededException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
}
