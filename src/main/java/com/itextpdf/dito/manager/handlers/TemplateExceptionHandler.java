package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.instance.deployment.InstanceDeploymentException;
import com.itextpdf.dito.manager.exception.template.TemplateBlockedByOtherUserException;
import com.itextpdf.dito.manager.exception.template.TemplateDeploymentException;
import com.itextpdf.dito.manager.exception.template.TemplatePreviewGenerationException;
import com.itextpdf.dito.manager.exception.template.TemplateVersionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TemplateExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(TemplateBlockedByOtherUserException.class)
    public ResponseEntity<ErrorResponseDTO> templateBlockedByOtherUserException(final TemplateBlockedByOtherUserException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InstanceDeploymentException.class)
    public ResponseEntity<ErrorResponseDTO> instanceDeploymentExceptionHandler(final InstanceDeploymentException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TemplateVersionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> templateVersionNotFoundExceptionHandler(final TemplateVersionNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TemplateDeploymentException.class)
    public ResponseEntity<ErrorResponseDTO> templateDeploymentExceptionHandler(final TemplateDeploymentException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TemplatePreviewGenerationException.class)
    public ResponseEntity<ErrorResponseDTO> templatePreviewGenerationExceptionHandler(final TemplatePreviewGenerationException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.CONFLICT);
    }
}
