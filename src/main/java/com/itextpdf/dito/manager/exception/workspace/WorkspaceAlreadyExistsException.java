package com.itextpdf.dito.manager.exception.workspace;

import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class WorkspaceAlreadyExistsException extends AbstractResourceAlreadyExistsException {
    private static final long serialVersionUID = 1L;

    public WorkspaceAlreadyExistsException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.WORKSPACE;
    }
}
