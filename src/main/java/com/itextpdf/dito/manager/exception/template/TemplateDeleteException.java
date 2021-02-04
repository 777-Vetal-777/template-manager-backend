package com.itextpdf.dito.manager.exception.template;

public class TemplateDeleteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Unable to delete template: ";

    public TemplateDeleteException(final String reason) {
        super(buildMessage(reason));
    }

    private static String buildMessage(final String reason) {
        return new StringBuilder().append(MESSAGE).append(reason).toString();
    }
}