package com.itextpdf.dito.manager.exception.instance;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class InstanceCustomHeaderValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public InstanceCustomHeaderValidationException(final String name) {
        this.message = buildMessage(name);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String name){
        final StringBuilder messageBuilder = new StringBuilder("HeaderName and HeaderValue in "); 
        messageBuilder.append(AliasConstants.INSTANCE);
        messageBuilder.append(" with name ");
        messageBuilder.append(name);
        messageBuilder.append(" have to be filled both");
        return messageBuilder.toString();
    }
}