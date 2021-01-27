package com.itextpdf.dito.manager.exception.instance;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class NotReachableInstanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public NotReachableInstanceException(final String socket) {
        message = buildMessage(socket);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String socket) {
        final StringBuilder result = new StringBuilder(AliasConstants.INSTANCE);
        result.append("'s socket is not reachable: ");
        result.append(socket);
        return result.toString();
    }
}
