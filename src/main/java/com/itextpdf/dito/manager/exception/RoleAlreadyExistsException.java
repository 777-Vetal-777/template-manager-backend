package com.itextpdf.dito.manager.exception;

public class RoleAlreadyExistsException extends RuntimeException {
    private String name;

    public RoleAlreadyExistsException(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
