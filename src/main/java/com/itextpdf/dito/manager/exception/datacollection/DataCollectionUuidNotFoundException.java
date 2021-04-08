package com.itextpdf.dito.manager.exception.datacollection;

import com.itextpdf.dito.manager.exception.AbstractResourceUuidNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class DataCollectionUuidNotFoundException extends AbstractResourceUuidNotFoundException {
    private static final long serialVersionUID = 1L;

    public DataCollectionUuidNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.DATA_COLLECTION;
    }
}
