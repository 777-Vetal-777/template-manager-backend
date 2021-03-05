package com.itextpdf.dito.manager.exception.resource;

public class ForbiddenOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Operation is forbidden for the current user.";

    public ForbiddenOperationException() {
        super(MESSAGE);
    }

}
