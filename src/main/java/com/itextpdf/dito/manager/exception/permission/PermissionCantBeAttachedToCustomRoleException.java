package com.itextpdf.dito.manager.exception.permission;

public class PermissionCantBeAttachedToCustomRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "Permission can't be attached to a custom role.";

    @Override
    public String getMessage() {
        return message;
    }
}
