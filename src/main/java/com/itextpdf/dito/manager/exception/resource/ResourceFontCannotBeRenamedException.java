package com.itextpdf.dito.manager.exception.resource;

public class ResourceFontCannotBeRenamedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Font resource cannot be renamed";

    public ResourceFontCannotBeRenamedException() {
        super(MESSAGE);
    }
}