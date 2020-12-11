package com.itextpdf.dito.manager.exception.instance;

import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class InstanceNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public InstanceNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.INSTANCE;
    }
}
