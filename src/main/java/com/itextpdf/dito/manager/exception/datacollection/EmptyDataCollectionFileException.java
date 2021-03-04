package com.itextpdf.dito.manager.exception.datacollection;

public class EmptyDataCollectionFileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Data collection's file couldn't be empty.";

    public EmptyDataCollectionFileException() {
        super(MESSAGE);
    }

}
