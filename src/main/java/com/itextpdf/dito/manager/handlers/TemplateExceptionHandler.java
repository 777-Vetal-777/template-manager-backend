package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.dto.error.TemplateImportErrorResponseDTO;
import com.itextpdf.dito.manager.exception.template.TemplateBlockedByOtherUserException;
import com.itextpdf.dito.manager.exception.template.TemplateCannotBeBlockedException;
import com.itextpdf.dito.manager.exception.template.TemplateCannotBePromotedException;
import com.itextpdf.dito.manager.exception.template.TemplateDeleteException;
import com.itextpdf.dito.manager.exception.template.TemplateDeploymentException;
import com.itextpdf.dito.manager.exception.template.TemplateHasWrongStructureException;
import com.itextpdf.dito.manager.exception.template.TemplateImportHasDuplicateNamesException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.exception.template.TemplatePreviewGenerationException;
import com.itextpdf.dito.manager.exception.template.TemplateProjectGenerationException;
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

    @ExceptionHandler(TemplateCannotBeBlockedException.class)
    public ResponseEntity<ErrorResponseDTO> templateCannotBeBlockedExceptionHandler(final TemplateCannotBeBlockedException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(TemplateHasWrongStructureException.class)
    public ResponseEntity<ErrorResponseDTO> templateHasWrongStructureExceptionHandler(final TemplateHasWrongStructureException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TemplateDeleteException.class)
    public ResponseEntity<ErrorResponseDTO> templateDeleteExceptionHandler(final TemplateDeleteException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TemplateProjectGenerationException.class)
    public ResponseEntity<ErrorResponseDTO> templateProjectGenerationExceptionHandler(final TemplateProjectGenerationException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TemplateImportProjectException.class)
    public ResponseEntity<ErrorResponseDTO> templateImportProjectExceptionHandler(final TemplateImportProjectException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TemplateImportHasDuplicateNamesException.class)
    public ResponseEntity<TemplateImportErrorResponseDTO> templateImportHasDuplicateNamesExceptionHandler(final TemplateImportHasDuplicateNamesException ex) {
        return new ResponseEntity<>(new TemplateImportErrorResponseDTO(ex.getMessage(), ex.getDuplicates()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TemplateCannotBePromotedException.class)
    public ResponseEntity<ErrorResponseDTO> templateCannotBePromotedExceptionHandler(final  TemplateCannotBePromotedException ex){
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }


}
