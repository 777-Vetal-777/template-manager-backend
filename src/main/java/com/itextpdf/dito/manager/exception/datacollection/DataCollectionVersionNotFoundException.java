package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class DataCollectionVersionNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public DataCollectionVersionNotFoundException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.DATA_COLLECTION;
    }
}