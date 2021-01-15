package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class DataCollectionFileSizeExceedLimitException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private final String message;

    public DataCollectionFileSizeExceedLimitException(final Long size) {
        message = buildMessage(size);

    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final Long size) {
        final StringBuilder result = new StringBuilder(AliasConstants.DATA_COLLECTION);
        result.append(" with size: ");
        result.append(size);
        result.append(" exceeds the size limit.");
        return result.toString();
    }
}
