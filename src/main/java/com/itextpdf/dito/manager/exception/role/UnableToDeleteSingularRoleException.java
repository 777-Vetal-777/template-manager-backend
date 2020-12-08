package com.itextpdf.dito.manager.exception.role;

public class UnableToDeleteSingularRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "A singular user's role can't be deleted.";

    @Override
    public String getMessage() {
        return message;
    }
}
