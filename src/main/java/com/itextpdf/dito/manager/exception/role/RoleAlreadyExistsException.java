package com.itextpdf.dito.manager.exception.role;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;

public class RoleAlreadyExistsException extends AbstractResourceAlreadyExistsException {
    private static final long serialVersionUID = 1L;

    public RoleAlreadyExistsException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.ROLE;
    }
}
