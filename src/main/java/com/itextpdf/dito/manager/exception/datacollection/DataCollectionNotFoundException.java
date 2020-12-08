package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;

public class DataCollectionNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public DataCollectionNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.DATA_COLLECTION;
    }
}
