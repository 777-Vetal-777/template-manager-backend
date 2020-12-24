package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class ResourceFileSizeExceedLimitException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public ResourceFileSizeExceedLimitException(final Long size) {
        message = buildMessage(size);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final Long size) {
        final StringBuilder result = new StringBuilder(AliasConstants.RESOURCE);
        result.append(" with size: ");
        result.append(size);
        result.append(" exceeds the size limit.");
        return result.toString();
    }
}
