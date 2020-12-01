package com.itextpdf.dito.manager.exception;

public class UserNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super();
    }
}

