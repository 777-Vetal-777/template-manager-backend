package com.itextpdf.dito.manager.exception.instance;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class NotReachableInstanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotReachableInstanceException(final String socket, final String errorMessage) {
        super(buildMessage(socket, errorMessage));
    }

    private static String buildMessage(final String socket, final String errorMessage) {
        final StringBuilder result = new StringBuilder(AliasConstants.INSTANCE);
        result.append("'s socket is not reachable: ");
        result.append(socket);
        result.append(". Error message:");
        result.append(errorMessage);
        return result.toString();
    }
}
