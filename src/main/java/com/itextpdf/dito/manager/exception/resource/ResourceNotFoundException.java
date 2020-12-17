package com.itextpdf.dito.manager.exception.resource;

import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class ResourceNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.RESOURCE;
    }
}
