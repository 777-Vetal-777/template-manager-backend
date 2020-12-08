package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;

public class TemplateFileNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public TemplateFileNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.TEMPLATE;
    }
}
