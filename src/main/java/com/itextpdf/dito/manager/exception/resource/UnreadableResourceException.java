package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class UnreadableResourceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;

    public UnreadableResourceException(final String name) {
        this.message = buildMessage(name);
    }

    private String buildMessage(final String name) {
        final StringBuilder message = new StringBuilder(AliasConstants.RESOURCE);
        message.append(" with name ");
        message.append(name);
        message.append(" can't be read.");
        return message.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
