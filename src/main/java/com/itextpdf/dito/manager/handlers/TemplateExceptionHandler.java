package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.template.TemplateBlockedByOtherUserException;
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
}
