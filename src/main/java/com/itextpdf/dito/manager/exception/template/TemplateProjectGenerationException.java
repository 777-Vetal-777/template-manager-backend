package com.itextpdf.dito.manager.exception.template;

public class TemplateProjectGenerationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Unable to generate template project: ";

    public TemplateProjectGenerationException(final String reason) {
        super(buildMessage(reason));
    }

    private static String buildMessage(final String reason) {
        return new StringBuilder().append(MESSAGE).append(reason).toString();
    }
}