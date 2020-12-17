package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class NoSuchResourceTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public NoSuchResourceTypeException(final String enumType) {
        message = buildMessage(enumType);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String enumType) {
        final StringBuilder result = new StringBuilder("No such ");
        result.append(AliasConstants.RESOURCE);
        result.append("'s type: ");
        result.append(enumType);
        return result.toString();
    }
}
