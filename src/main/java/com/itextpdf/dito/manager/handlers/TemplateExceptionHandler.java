package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.template.TemplateBlockedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TemplateExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(TemplateBlockedException.class)
    public ResponseEntity<ErrorResponseDTO> templateBlockedExceptionHandler(final TemplateBlockedException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage()), HttpStatus.FORBIDDEN);
    }
}
