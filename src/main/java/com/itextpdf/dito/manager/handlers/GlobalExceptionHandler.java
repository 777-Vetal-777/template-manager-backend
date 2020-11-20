package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.exception.TemplateNameAlreadyRegisteredException;
import com.itextpdf.dito.manager.exception.UnsupportedTemplateTypeException;
import com.itextpdf.dito.manager.exception.UserAlreadyExistsException;
import com.itextpdf.dito.manager.exception.UserNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> userNotFoundErrorHandler(
            final UserNotFoundException ex) {
        log.error(ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid request parameter", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> userAlreadyExistsExceptionHandler(
            final UserAlreadyExistsException ex) {
        log.error(ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid request parameter", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDTO> invalidRefreshToken(final InvalidRefreshTokenException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid refresh token", ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> emptyParamsValidationErrorHandler(
            final MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        final String errorMsg =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .findFirst()
                        .orElse(ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponseDTO("Validation error", errorMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedTemplateTypeException.class)
    public ResponseEntity<ErrorResponseDTO> unsupportedTemplateType(final UnsupportedTemplateTypeException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("Unknown template's type",
                new StringBuilder("Unknown type: ").append(ex.getType()).toString()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TemplateNameAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponseDTO> templateNameAlreadyIsRegistered(
            final TemplateNameAlreadyRegisteredException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("Template's name is already registered",
                new StringBuilder("Template's name: ").append(ex.getTemplateName()).toString()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponseDTO> userAccountIsLocked(final LockedException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("Account is locked.", "Please contact an administrator."),
                HttpStatus.LOCKED);
    }
}
