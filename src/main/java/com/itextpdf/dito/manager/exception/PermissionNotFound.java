package com.itextpdf.dito.manager.exception;

public class PermissionNotFound extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;

    public PermissionNotFound(String message) {
        super(message);
    }

    public PermissionNotFound() {
        super();
    }
}
