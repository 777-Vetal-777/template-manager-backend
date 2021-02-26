package com.itextpdf.dito.manager.exception.workspace;

public class WorkspaceHasBlankParameterException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WorkspaceHasBlankParameterException(final String parameter) {
       super(buildMessage(parameter));
    }

    private static String buildMessage(final String parameter) {
        final StringBuilder result = new StringBuilder("Parameter with name: ");
        result.append(parameter);
        result.append(" could not be blank.");
        return result.toString();
    }
}
