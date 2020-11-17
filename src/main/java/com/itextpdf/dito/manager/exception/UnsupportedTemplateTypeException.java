package com.itextpdf.dito.manager.exception;

public class UnsupportedTemplateTypeException extends RuntimeException {
    private String unsupportedTemplateType;

    public String getUnsupportedTemplateType() {
        return unsupportedTemplateType;
    }

    public void setUnsupportedTemplateType(String unsupportedTemplateType) {
        this.unsupportedTemplateType = unsupportedTemplateType;
    }
}
