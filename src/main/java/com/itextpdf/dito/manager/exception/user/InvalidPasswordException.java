package com.itextpdf.dito.manager.exception.user;

public class InvalidPasswordException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Password is wrong.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }
}
