package com.itextpdf.dito.manager.exception.user;

public class PasswordNotSpecifiedByAdminException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "Password is not specified by admin";

    public PasswordNotSpecifiedByAdminException() {
        super(message);
    }
}
