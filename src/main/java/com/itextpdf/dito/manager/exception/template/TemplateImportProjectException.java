package com.itextpdf.dito.manager.exception.template;

public class TemplateImportProjectException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Import template failed: ";

    public TemplateImportProjectException(Throwable cause) {
        super(MESSAGE.concat(cause.getMessage()), cause);
    }

    public TemplateImportProjectException(String message, Throwable cause) {
        super(message, cause);
    }

}
