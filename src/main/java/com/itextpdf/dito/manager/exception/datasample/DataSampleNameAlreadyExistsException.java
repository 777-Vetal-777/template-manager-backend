package com.itextpdf.dito.manager.exception.datasample;


public class DataSampleNameAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "This name is already exists. Please add another name";

    @Override
    public String getMessage() {
        return message;
    }
}
