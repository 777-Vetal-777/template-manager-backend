package com.itextpdf.dito.manager.exception.datacollection;

public class DataCollectionHasDependenciesException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "Data collection has outbound dependencies";

    @Override
    public String getMessage() {
        return message;
    }
}
