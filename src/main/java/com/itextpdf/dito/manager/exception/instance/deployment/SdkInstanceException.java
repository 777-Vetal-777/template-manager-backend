package com.itextpdf.dito.manager.exception.instance.deployment;

public class SdkInstanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public SdkInstanceException(final String errorMessage, final String instanceSocket, final Integer responseCode, final String responseMessage) {
        this.errorMessage = buildMessage(errorMessage, instanceSocket, responseCode, responseMessage);
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    private String buildMessage(final String errorMessage, final String instanceSocket, final Integer responseCode, final String responseMessage){
        final StringBuilder messageBuilder = new StringBuilder(errorMessage);
        messageBuilder.append(" Socket: ");
        messageBuilder.append(instanceSocket);
        messageBuilder.append(". Response code: ");
        messageBuilder.append(responseCode);
        messageBuilder.append(". Response message: ");
        messageBuilder.append(responseMessage);
        return messageBuilder.toString();
    }
}
