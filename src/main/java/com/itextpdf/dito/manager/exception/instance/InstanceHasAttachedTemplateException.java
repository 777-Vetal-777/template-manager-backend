package com.itextpdf.dito.manager.exception.instance;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class InstanceHasAttachedTemplateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;

    public InstanceHasAttachedTemplateException(final String name) {
        message = buildMessage(name);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String name) {
        final StringBuilder result = new StringBuilder("You can't disconnect ");
        result.append(AliasConstants.INSTANCE);
        result.append(" with name ");
        result.append(name);
        result.append(". It has templates connected.");
        return result.toString();
    }
}
