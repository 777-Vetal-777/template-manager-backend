package com.itextpdf.dito.manager.exception;

public abstract class AbstractResourceUuidNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    protected AbstractResourceUuidNotFoundException(final String uuid) {
        this.message = buildMessage(uuid);
    }

    protected AbstractResourceUuidNotFoundException() {
        this.message = buildMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String uuid) {
        final StringBuilder messageBuilder = new StringBuilder(getResourceAlias());
        messageBuilder.append(" with uuid ");
        messageBuilder.append(uuid);
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
