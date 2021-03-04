package com.itextpdf.dito.manager.exception.resource;

public class ResourceHasDependenciesException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Resource has outbound dependencies";

    public ResourceHasDependenciesException() {
        super(MESSAGE);
    }
}
