package com.itextpdf.dito.manager.exception;

public class TemplateNameAlreadyRegisteredException extends RuntimeException {
    private String templateName;

    public TemplateNameAlreadyRegisteredException(final String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
