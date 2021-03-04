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
        final StringBuilder messageBuilder = new StringBuilder(getResourceAlias());
        messageBuilder.append(" with id ");
        messageBuilder.append(resourceId);
        messageBuilder.append(" already exists.");
        return messageBuilder.toString();
    }

    protected abstract String getResourceAlias();
}
