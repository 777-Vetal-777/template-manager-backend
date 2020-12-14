package com.itextpdf.dito.manager.exception.instance;

import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class InstanceAlreadyExistsException extends AbstractResourceAlreadyExistsException {
    private static final long serialVersionUID = 1L;

    public InstanceAlreadyExistsException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.INSTANCE;
    }
}
