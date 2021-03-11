package com.itextpdf.dito.manager.handlers;

import com.itextpdf.dito.manager.dto.error.ErrorResponseDTO;
import com.itextpdf.dito.manager.exception.instance.DefaultInstanceException;
import com.itextpdf.dito.manager.exception.instance.InstanceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.instance.InstanceCustomHeaderValidationException;
import com.itextpdf.dito.manager.exception.instance.InstanceHasAttachedTemplateException;
import com.itextpdf.dito.manager.exception.instance.InstanceUsedInPromotionPathException;
import com.itextpdf.dito.manager.exception.instance.NotReachableInstanceException;

import com.itextpdf.dito.manager.exception.instance.deployment.SdkInstanceException;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceHasNoDevelopmentStageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InstanceExceptionHandler extends AbstractExceptionHandler {
    @ExceptionHandler(NotReachableInstanceException.class)
    public ResponseEntity<ErrorResponseDTO> notReachableInstanceExceptionHandler(
            final NotReachableInstanceException ex) {
        return buildErrorResponse(ex, "Not reachable instance.", HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(InstanceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> instanceAlreadyExistsExceptionHandler(
            final InstanceAlreadyExistsException ex) {
        return buildErrorResponse(ex, ex.getMessage(), HttpStatus.CONFLICT);
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

    @ExceptionHandler(SdkInstanceException.class)
    public ResponseEntity<ErrorResponseDTO> sdkInstanceExceptionHandler(
            final SdkInstanceException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefaultInstanceException.class)
    public ResponseEntity<ErrorResponseDTO> defaultInstanceExceptionHandler(
            final WorkspaceHasNoDevelopmentStageException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(InstanceCustomHeaderValidationException.class)
	public ResponseEntity<ErrorResponseDTO> instanceCustomHeaderValidationExceptionHandler(
			final InstanceCustomHeaderValidationException ex) {
		return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

}
