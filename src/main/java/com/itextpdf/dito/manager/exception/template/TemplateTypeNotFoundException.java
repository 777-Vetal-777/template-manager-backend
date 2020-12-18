package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;

public class TemplateTypeNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public TemplateTypeNotFoundException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.TEMPLATE_TYPE;
    }
}
