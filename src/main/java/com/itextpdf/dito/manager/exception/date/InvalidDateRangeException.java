package com.itextpdf.dito.manager.exception.date;

public class InvalidDateRangeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Date range should contain two elements: start date and end date";

    public InvalidDateRangeException() {
        super(MESSAGE);
    }

}
