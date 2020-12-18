package com.itextpdf.dito.manager.exception.role;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;

public class RoleNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public RoleNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.ROLE;
    }

}
