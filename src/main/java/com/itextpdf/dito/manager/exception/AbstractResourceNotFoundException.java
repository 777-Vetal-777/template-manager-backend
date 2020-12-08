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
        final StringBuilder message = new StringBuilder(getResourceAlias());
        message.append(" with id ");
        message.append(resourceId);
        message.append(" is not found.");
        return message.toString();
    }

    private String buildMessage() {
        final StringBuilder message = new StringBuilder("No ");
        message.append(getResourceAlias());
        message.append("s are found.");
        return message.toString();
    }

    protected abstract String getResourceAlias();
}
