package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class NoSuchDataCollectionTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public NoSuchDataCollectionTypeException(String enumType) {
        message = buildMessage(enumType);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String enumType) {
        final StringBuilder result = new StringBuilder("No such ");
        result.append(AliasConstants.DATA_COLLECTION);
        result.append("'s type: ");
        result.append(enumType);
        return result.toString();
    }
}
