package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class InvalidDataCollectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = AliasConstants.DATA_COLLECTION + " is not valid.";

    public InvalidDataCollectionException() {
        super(MESSAGE);
    }
}
