package com.itextpdf.dito.manager.exception.mail;

public class DailyMailQuotaExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "Daily user sending quota exceeded.";

    public DailyMailQuotaExceededException() {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
