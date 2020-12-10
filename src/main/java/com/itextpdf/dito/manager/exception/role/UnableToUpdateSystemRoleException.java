package com.itextpdf.dito.manager.exception.role;

public class UnableToUpdateSystemRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "System roles updating is denied.";

    @Override
    public String getMessage() {
        return message;
    }
}
