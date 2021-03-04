package com.itextpdf.dito.manager.exception.role;

public class UnableToUpdateSystemRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "System roles updating is denied.";

    public UnableToUpdateSystemRoleException() {
        super(MESSAGE);
    }
}
