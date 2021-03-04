package com.itextpdf.dito.manager.exception.token;

public class InvalidRefreshTokenException extends Exception {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Invalid or illegal refresh token";

    public InvalidRefreshTokenException() {
        super(MESSAGE);
    }
}
