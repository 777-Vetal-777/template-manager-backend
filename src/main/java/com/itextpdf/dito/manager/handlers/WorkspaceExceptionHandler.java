package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.workspace.OnlyOneWorkspaceAllowedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WorkspaceExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(OnlyOneWorkspaceAllowedException.class)
    public ResponseEntity<ErrorResponseDTO> onlyOneWorkspaceExceptionHandler(
            final OnlyOneWorkspaceAllowedException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
}
