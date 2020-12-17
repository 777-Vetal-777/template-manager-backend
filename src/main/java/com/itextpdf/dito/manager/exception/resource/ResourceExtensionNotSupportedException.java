package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class ResourceExtensionNotSupportedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public ResourceExtensionNotSupportedException(final String extension) {
        message = buildMessage(extension);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String extension) {
        final StringBuilder result = new StringBuilder(AliasConstants.RESOURCE);
        result.append(" with extension: ");
        result.append(extension);
        result.append(" not supported.");
        return result.toString();
    }
}
