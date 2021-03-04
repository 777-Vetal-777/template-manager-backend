package com.itextpdf.dito.manager.exception;

public abstract class AbstractResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    protected AbstractResourceNotFoundException(final String resourceId) {
        this.message = buildMessage(resourceId);
    }

    protected AbstractResourceNotFoundException() {
        this.message = buildMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String resourceId) {
        final StringBuilder messageBuilder = new StringBuilder(getResourceAlias());
        messageBuilder.append(" with id ");
        messageBuilder.append(resourceId);
        messageBuilder.append(" is not found.");
        return messageBuilder.toString();
    }

    private String buildMessage() {
        final StringBuilder messageBuilder = new StringBuilder("No ");
        messageBuilder.append(getResourceAlias());
        messageBuilder.append("s are found.");
        return messageBuilder.toString();
    }

    protected abstract String getResourceAlias();
}
