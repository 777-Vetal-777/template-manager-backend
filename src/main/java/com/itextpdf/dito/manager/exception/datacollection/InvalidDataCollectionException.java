package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class InvalidDataCollectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = AliasConstants.DATA_COLLECTION + " is not valid.";

    @Override
    public String getMessage() {
        return message;
    }
}
