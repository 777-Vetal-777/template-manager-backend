package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class TemplateVersionNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public TemplateVersionNotFoundException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.TEMPLATE_VERSION;
    }
}
