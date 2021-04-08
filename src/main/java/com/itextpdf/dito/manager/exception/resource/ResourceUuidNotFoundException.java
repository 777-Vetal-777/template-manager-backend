package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AbstractResourceUuidNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class ResourceUuidNotFoundException extends AbstractResourceUuidNotFoundException {
    private static final long serialVersionUID = 1L;

    public ResourceUuidNotFoundException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.RESOURCE;
    }
}
