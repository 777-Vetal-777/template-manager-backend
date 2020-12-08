package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;

public class DataCollectionAlreadyExistsException extends AbstractResourceAlreadyExistsException {
    private static final long serialVersionUID = 1L;

    public DataCollectionAlreadyExistsException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.DATA_COLLECTION;
    }
}
