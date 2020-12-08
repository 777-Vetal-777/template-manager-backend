package com.itextpdf.dito.manager.exception.user;

public class InvalidPasswordException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "Password is wrong.";

    public InvalidPasswordException() {
        super(message);
    }
}
