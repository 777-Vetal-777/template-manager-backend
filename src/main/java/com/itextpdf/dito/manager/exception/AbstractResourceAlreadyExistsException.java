package com.itextpdf.dito.manager.exception;

public abstract class AbstractResourceAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    protected AbstractResourceAlreadyExistsException(final String resourceId) {
        this.message = buildMessage(resourceId);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String resourceId) {
        final StringBuilder message = new StringBuilder(getResourceAlias());
        message.append(" with id ");
        message.append(resourceId);
        message.append(" already exists.");
        return message.toString();
    }

    protected abstract String getResourceAlias();
}
