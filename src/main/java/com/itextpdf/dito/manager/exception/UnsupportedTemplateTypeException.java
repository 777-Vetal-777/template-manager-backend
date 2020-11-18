package com.itextpdf.dito.manager.exception;

public class UnsupportedTemplateTypeException extends RuntimeException {
    private String type;

    public UnsupportedTemplateTypeException(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
