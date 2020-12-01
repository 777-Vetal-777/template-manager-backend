package com.itextpdf.dito.manager.exception;

public class RoleNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException() {
        super();
    }
}
