package com.itextpdf.dito.manager.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
