package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.dto.error.RequestParamErrorResponseDTO;
import com.itextpdf.dito.manager.exception.ChangePasswordException;
import com.itextpdf.dito.manager.exception.CollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.FileCannotBeReadException;
import com.itextpdf.dito.manager.exception.FileTypeNotSupportedException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static java.util.stream.Collectors.toList;

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

    @ExceptionHandler(ChangePasswordException.class)
    public ResponseEntity<ErrorResponseDTO> changePasswordExceptionHandler(
            final ChangePasswordException ex) {
        log.error(ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid request parameter", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDTO> invalidRefreshToken(final InvalidRefreshTokenException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid refresh token", ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RequestParamErrorResponseDTO> requestParamsValidationErrorHandler(
            final MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(toList());
        return new ResponseEntity<>(
                new RequestParamErrorResponseDTO("Validation error", "Some request params are invalid", fieldErrors), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> collectionAlreadyExists(final CollectionAlreadyExistsException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("Collections with name already exist.",
                new StringBuilder("Collection's name: ").append(ex.getCollectionName()).toString()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> fileTypeNotSupported(final FileTypeNotSupportedException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("File type is not supported",
                new StringBuilder("File type: ").append(ex.getFileType()).toString()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> fileCannotBeRead(final FileCannotBeReadException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("File cannot be read",
                new StringBuilder("File name: ").append(ex.getFileName()).toString()),
                HttpStatus.BAD_REQUEST);
    }
}
