package com.itextpdf.dito.manager.exception;

public class TemplateNameAlreadeIsRegisteredException extends RuntimeException {
    private String templateName;

    public TemplateNameAlreadeIsRegisteredException(final String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
