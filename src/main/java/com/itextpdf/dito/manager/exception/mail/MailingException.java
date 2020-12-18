package com.itextpdf.dito.manager.exception.mail;

public class MailingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MailingException(final String message) {
        super(message);
    }
}
