package com.itextpdf.dito.manager.exception.instance;

public class DefaultInstanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Instance on dev stage doesn't meet default instance requirements";

    public DefaultInstanceException() {
        super(MESSAGE);
    }
}
