package com.itextpdf.dito.manager.exception.role;

public class UnableToSetPermissionsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private static final String message = "Permissions for this role are pre-set and can't be edited.";

    @Override
    public String getMessage() {
        return message;
    }

}
