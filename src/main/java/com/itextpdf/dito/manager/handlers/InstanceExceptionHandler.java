package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.instance.InstanceHasAttachedTemplateException;
import com.itextpdf.dito.manager.exception.instance.InstanceUsedInPromotionPathException;
import com.itextpdf.dito.manager.exception.instance.NotReachableInstanceException;

import com.itextpdf.dito.manager.exception.instance.deployment.InstanceRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InstanceExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(NotReachableInstanceException.class)
    public ResponseEntity<ErrorResponseDTO> notReachableInstanceExceptionHandler(
            final NotReachableInstanceException ex) {
        return buildErrorResponse(ex, "Not reachable instance.", HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(InstanceUsedInPromotionPathException.class)
    public ResponseEntity<ErrorResponseDTO> notReachableInstanceExceptionHandler(
            final InstanceUsedInPromotionPathException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InstanceHasAttachedTemplateException.class)
    public ResponseEntity<ErrorResponseDTO> instanceHasAttachedTemplateExceptionHandler(
            final InstanceHasAttachedTemplateException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InstanceRegistrationException.class)
    public ResponseEntity<ErrorResponseDTO> instanceRegistrationExceptionHandler(
            final InstanceHasAttachedTemplateException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_GATEWAY);
    }
}
