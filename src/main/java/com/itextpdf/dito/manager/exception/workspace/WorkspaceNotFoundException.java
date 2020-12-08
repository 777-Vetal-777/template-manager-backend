package com.itextpdf.dito.manager.exception.workspace;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;

public class WorkspaceNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public WorkspaceNotFoundException(final String id) {
        super(id);
    }

    public WorkspaceNotFoundException() {
        super();
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.WORKSPACE;
    }
}
