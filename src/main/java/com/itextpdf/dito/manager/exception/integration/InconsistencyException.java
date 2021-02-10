package com.itextpdf.dito.manager.exception.integration;

public class InconsistencyException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final static String MESSAGE = "Data inconsistency error";

    public InconsistencyException() {
        super(MESSAGE);
    }
  
}

