package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.stage.NoNextStageOnPromotionPathException;
import com.itextpdf.dito.manager.exception.workspace.OnlyOneWorkspaceAllowedException;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceHasNoDevelopmentStageException;
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

    @ExceptionHandler(WorkspaceHasNoDevelopmentStageException.class)
    public ResponseEntity<ErrorResponseDTO> workspaceHasNoDevelopmentStageExceptionHandler(
            final WorkspaceHasNoDevelopmentStageException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoNextStageOnPromotionPathException.class)
    public ResponseEntity<ErrorResponseDTO> noNextStageOnPromotionPathExceptionHandler(final NoNextStageOnPromotionPathException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

}
