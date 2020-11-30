package com.itextpdf.dito.manager.dto.error;

import java.util.List;

public class RequestParamErrorResponseDTO extends ErrorResponseDTO {
    private List<String> fieldErrors;

    public List<String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public RequestParamErrorResponseDTO(String message, String details, List<String> fieldErrors) {
        super(message, details);
        this.fieldErrors = fieldErrors;
    }
}
