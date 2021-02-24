package com.itextpdf.dito.manager.exception.instance;

import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class InstanceAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public InstanceAlreadyExistsException(final String param) {
        this.message = buildMessage(param);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String param){
        final StringBuilder message = new StringBuilder(AliasConstants.INSTANCE);
        message.append(" with this name or URL already exists: ");
        message.append(param);
        return message.toString();
    }
}
