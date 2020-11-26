package com.itextpdf.dito.manager.exception;

public class RoleCannotBeRemovedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RoleCannotBeRemovedException(String message) {
        super(message);
    }
}
