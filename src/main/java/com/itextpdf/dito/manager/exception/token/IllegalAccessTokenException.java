package com.itextpdf.dito.manager.exception.token;

public class IllegalAccessTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Illegal access token.";

    public IllegalAccessTokenException() {
        super(MESSAGE);
    }
}
