package com.itextpdf.dito.manager.exception.role;

public class AttemptToDeleteSystemRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "System role can't be deleted.";

    public AttemptToDeleteSystemRoleException() {
        super(MESSAGE);
    }
}
