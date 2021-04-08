package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AbstractResourceUuidNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class TemplateUuidNotFoundException extends AbstractResourceUuidNotFoundException {
    private static final long serialVersionUID = 1L;

    public TemplateUuidNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.TEMPLATE;
    }
}
