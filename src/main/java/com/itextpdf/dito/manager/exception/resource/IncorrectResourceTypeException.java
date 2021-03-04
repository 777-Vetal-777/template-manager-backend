package com.itextpdf.dito.manager.exception.resource;

public class IncorrectResourceTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Resource type not supported: ";

    public IncorrectResourceTypeException(final String resourceType) {
        super(new StringBuilder(MESSAGE).append(resourceType).toString());
    }
}
