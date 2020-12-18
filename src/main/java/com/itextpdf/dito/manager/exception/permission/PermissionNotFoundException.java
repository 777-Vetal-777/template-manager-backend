package com.itextpdf.dito.manager.exception.permission;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;

public class PermissionNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public PermissionNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.PERMISSION;
    }
}
