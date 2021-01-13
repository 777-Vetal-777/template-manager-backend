package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.resource.ForbiddenOperationException;
import com.itextpdf.dito.manager.exception.resource.PermissionIsNotAllowedForResourceTypeException;
import com.itextpdf.dito.manager.exception.resource.ResourceExtensionNotSupportedException;
import com.itextpdf.dito.manager.exception.resource.ResourceFileSizeExceedLimitException;
import com.itextpdf.dito.manager.exception.resource.UnreadableResourceException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(ResourceFileSizeExceedLimitException.class)
    public ResponseEntity<ErrorResponseDTO> resourceFileSizeExceedLimitExceptionHandler(
            final ResourceFileSizeExceedLimitException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceExtensionNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> resourceExtensionNotSupportedExceptionHandler(
            final ResourceExtensionNotSupportedException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnreadableResourceException.class)
    public ResponseEntity<ErrorResponseDTO> unreadableExceptionHandler(final UnreadableResourceException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponseDTO> forbiddenOperationExceptionHandler(final ForbiddenOperationException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PermissionIsNotAllowedForResourceTypeException.class)
    public ResponseEntity<ErrorResponseDTO> permissionIsNotAllowedForResourceTypeExceptionHandler(
            final PermissionIsNotAllowedForResourceTypeException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
