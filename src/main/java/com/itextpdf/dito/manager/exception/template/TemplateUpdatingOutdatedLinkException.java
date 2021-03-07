package com.itextpdf.dito.manager.exception.template;

public class TemplateUpdatingOutdatedLinkException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Problem with updating outdated nested component references.";

    public TemplateUpdatingOutdatedLinkException() {
        super(MESSAGE);
    }
}
