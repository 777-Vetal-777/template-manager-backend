package com.itextpdf.dito.manager.dto.error;

public class ErrorResponseDTO {
    private String message;
    private String details;

    public ErrorResponseDTO() {

    }

    public ErrorResponseDTO(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public ErrorResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
