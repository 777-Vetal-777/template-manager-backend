package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class UnreadableResourceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public UnreadableResourceException(final String name) {
        this.message = buildMessage(name);
    }

    private String buildMessage(final String name) {
        final StringBuilder messageBuilder = new StringBuilder(AliasConstants.RESOURCE);
        messageBuilder.append(" with name ");
        messageBuilder.append(name);
        messageBuilder.append(" can't be read.");
        return messageBuilder.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
