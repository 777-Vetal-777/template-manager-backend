package com.itextpdf.dito.manager.exception.user;

public class NewPasswordTheSameAsOldPasswordException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "The new password equals to the old password.";

    public NewPasswordTheSameAsOldPasswordException() {
        super(message);
    }
}
