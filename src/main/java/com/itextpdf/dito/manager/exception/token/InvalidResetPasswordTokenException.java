package com.itextpdf.dito.manager.exception.token;

public class InvalidResetPasswordTokenException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private static final String message = "Invalid or illegal reset password token";

    @Override
    public String getMessage() {
        return message;
    }
}
