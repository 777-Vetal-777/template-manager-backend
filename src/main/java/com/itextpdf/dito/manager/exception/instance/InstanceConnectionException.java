package com.itextpdf.dito.manager.exception.instance;

public class InstanceConnectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public InstanceConnectionException(final Integer responseCode, final String responseMessage) {
        this.errorMessage = buildMessage(responseCode, responseMessage);
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    private String buildMessage(final Integer responseCode, final String responseMessage){
        final StringBuilder messageBuilder = new StringBuilder("Failed to register API instance. ");
        messageBuilder.append("Response code: ");
        messageBuilder.append(responseCode);
        messageBuilder.append(". Response message: ");
        messageBuilder.append(responseMessage);
        return messageBuilder.toString();
    }
}
