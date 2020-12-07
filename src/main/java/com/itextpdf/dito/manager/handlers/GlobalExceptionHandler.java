package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.dto.error.RequestParamErrorResponseDTO;
import com.itextpdf.dito.manager.exception.ChangePasswordException;
import com.itextpdf.dito.manager.exception.CollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.EntityNotFoundException;
import com.itextpdf.dito.manager.exception.FileCannotBeReadException;
import com.itextpdf.dito.manager.exception.FileTypeNotSupportedException;
import com.itextpdf.dito.manager.exception.GlobalAdminAlreadyExistsException;
import com.itextpdf.dito.manager.exception.InvalidPasswordException;
import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.exception.PermissionCantBeAttachedToCustomRole;
import com.itextpdf.dito.manager.exception.RoleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.RoleCannotBeRemovedException;
import com.itextpdf.dito.manager.exception.TemplateNameAlreadyRegisteredException;
import com.itextpdf.dito.manager.exception.UnsupportedSortFieldException;
import com.itextpdf.dito.manager.exception.UnsupportedTemplateTypeException;
import com.itextpdf.dito.manager.exception.UserAlreadyExistsException;
import com.itextpdf.dito.manager.exception.WorkspaceNameAlreadyExistsException;
import com.itextpdf.dito.manager.exception.WorkspaceNotFoundException;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> userNotFoundErrorHandler(
            final EntityNotFoundException ex) {
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
                new ErrorResponseDTO("Invalid request parameter", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> invalidPasswordExceptionHandler(
            final InvalidPasswordException ex) {
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
        final List<String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new StringBuilder().append(error.getField()).append(": ").append(error.getDefaultMessage()).toString())
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
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WorkspaceNameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> workspaceNameAlreadyExists(final WorkspaceNameAlreadyExistsException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("Workspace with that name already exists", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WorkspaceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> workspaceNotFound(final WorkspaceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO("Workspace does not exists", ex.getMessage()),
                HttpStatus.NOT_FOUND);
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

    @ExceptionHandler(UnsupportedSortFieldException.class)
    public ResponseEntity<ErrorResponseDTO> unsupportedSortFieldHandler(final UnsupportedSortFieldException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid sort parameter", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleCannotBeRemovedException.class)
    public ResponseEntity<ErrorResponseDTO> roleCannotBeRemovedExceptionHandler(final RoleCannotBeRemovedException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid sort parameter", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionCantBeAttachedToCustomRole.class)
    public ResponseEntity<ErrorResponseDTO> permissionCantBeAttachedToRole(final PermissionCantBeAttachedToCustomRole ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Can't attach permission to the specified role", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> roleAlreadyExistsExceptionHandler(final RoleAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Role already exists", ex.getName()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GlobalAdminAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> roleAlreadyExistsExceptionHandler(final GlobalAdminAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Global administrator already exists", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
