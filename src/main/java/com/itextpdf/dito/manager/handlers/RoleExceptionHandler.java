package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.permission.PermissionCantBeAttachedToCustomRoleException;
import com.itextpdf.dito.manager.exception.role.AttemptToAttachGlobalAdministratorRoleException;
import com.itextpdf.dito.manager.exception.role.AttemptToDeleteSystemRoleException;
import com.itextpdf.dito.manager.exception.role.UnableToDeleteSingularRoleException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RoleExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(AttemptToDeleteSystemRoleException.class)
    public ResponseEntity<ErrorResponseDTO> attemptToDeleteSystemRoleExceptionHandler(
            final AttemptToDeleteSystemRoleException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnableToDeleteSingularRoleException.class)
    public ResponseEntity<ErrorResponseDTO> unableToDeleteSingularRoleExceptionHandler(
            final UnableToDeleteSingularRoleException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionCantBeAttachedToCustomRoleException.class)
    public ResponseEntity<ErrorResponseDTO> permissionCantBeAttachedToCustomRoleExceptionHandler(
            final PermissionCantBeAttachedToCustomRoleException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AttemptToAttachGlobalAdministratorRoleException.class)
    public ResponseEntity<ErrorResponseDTO> attemptToAttachGlobalAdministratorRoleExceptionHandler(
            final AttemptToAttachGlobalAdministratorRoleException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
}
