package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class PermissionIsNotAllowedForResourceTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public PermissionIsNotAllowedForResourceTypeException(final String permission) {
        message = buildMessage(permission);
    }

    private String buildMessage(final String permission) {
        final StringBuilder result = new StringBuilder(AliasConstants.PERMISSION + " ");
        result.append(permission);
        result.append(" is not allowed for this type of resource.");
        return result.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
