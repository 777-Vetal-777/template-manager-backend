package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.user.InvalidPasswordException;
import com.itextpdf.dito.manager.exception.user.NewPasswordTheSameAsOldPasswordException;

import com.itextpdf.dito.manager.exception.user.UserNotFoundOrNotActiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(NewPasswordTheSameAsOldPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> newPasswordTheSameAsOldPasswordExceptionHandler(
            final NewPasswordTheSameAsOldPasswordException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> invalidPasswordExceptionHandler(
            final InvalidPasswordException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponseDTO> lockedExceptionHandler(final LockedException ex) {
        return buildErrorResponse(ex, "Account is locked.", HttpStatus.LOCKED);
    }

    @ExceptionHandler(UserNotFoundOrNotActiveException.class)
    public ResponseEntity<ErrorResponseDTO> userNotFoundExceptionHandler(final UserNotFoundOrNotActiveException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }
}
