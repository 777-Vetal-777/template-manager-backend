package com.itextpdf.dito.manager.exception.instance;

public class InstanceConnectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "Instance is already used";

    public InstanceConnectionException() {
        super(message);
    }
}
