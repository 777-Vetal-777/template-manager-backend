package com.itextpdf.dito.manager.exception.role;

public class UnableToDeleteSingularRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "User must have at least an one role.";

    public UnableToDeleteSingularRoleException() {
        super(MESSAGE);
    }
}
