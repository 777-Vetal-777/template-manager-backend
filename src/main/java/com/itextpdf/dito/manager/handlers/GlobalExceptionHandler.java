package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.Base64DecodeException;
import com.itextpdf.dito.manager.exception.datacollection.NoSuchDataCollectionTypeException;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.mail.MailingException;
import com.itextpdf.dito.manager.exception.sort.UnsupportedSortFieldException;
import com.itextpdf.dito.manager.exception.template.AbstractResourceInvalidNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(AbstractResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> resourceNotFoundExceptionHandler(
            final AbstractResourceNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AbstractResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> abstractResourceAlreadyExistsExceptionHandler(final AbstractResourceAlreadyExistsException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AbstractResourceInvalidNameException.class)
    public ResponseEntity<ErrorResponseDTO> abstractResourceInvalidNameExceptionHandler(final AbstractResourceInvalidNameException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(MailingException.class)
    public ResponseEntity<ErrorResponseDTO> dailyMailQuotaExceededExceptionHandler(final MailingException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(NoSuchDataCollectionTypeException.class)
    public ResponseEntity<ErrorResponseDTO> noSuchEnumTypeExceptionHandler(final NoSuchDataCollectionTypeException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> maxUploadSizeExeededExceptionHandler(final MaxUploadSizeExceededException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponseDTO> invalidDateExceptionHandler(final InvalidDateRangeException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Base64DecodeException.class)
    public ResponseEntity<ErrorResponseDTO> base64DecodeExceptionHandler(final Base64DecodeException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
}
