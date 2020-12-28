package com.itextpdf.dito.manager.exception.resource;

public class ForbiddenOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message = "Operation is forbidden for the current user.";

    @Override
    public String getMessage() {
        return message;
    }

}
