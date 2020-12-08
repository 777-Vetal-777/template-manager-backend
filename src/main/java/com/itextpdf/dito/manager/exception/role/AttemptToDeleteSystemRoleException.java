package com.itextpdf.dito.manager.exception.role;

public class AttemptToDeleteSystemRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "System role can't be deleted.";

    @Override
    public String getMessage() {
        return message;
    }
}
