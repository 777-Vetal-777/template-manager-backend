package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.token.InvalidRefreshTokenException;

import com.itextpdf.dito.manager.exception.token.InvalidResetPasswordTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TokenExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDTO> invalidRefreshTokenExceptionHandler(final InvalidRefreshTokenException ex) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidResetPasswordTokenException.class)
    public ResponseEntity<ErrorResponseDTO> invalidResetPasswordTokenExceptionHandler(final InvalidResetPasswordTokenException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
}
