package com.itextpdf.dito.manager.exception;

public class Base64DecodeException extends RuntimeException {

    private final String message;

    public Base64DecodeException(final String argument) {
        this.message = buildMessage(argument);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String argument) {
        final StringBuilder messageBuilder = new StringBuilder("Param was not URL-safe encoded: ");
        messageBuilder.append(argument);
        return messageBuilder.toString();
    }
}