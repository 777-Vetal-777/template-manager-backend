package com.itextpdf.dito.manager.exception.datasample;

import com.itextpdf.dito.manager.exception.AbstractResourceUuidNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class DataSampleUuidNotFoundException extends AbstractResourceUuidNotFoundException {
    private static final long serialVersionUID = 1L;

    public DataSampleUuidNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.DATA_SAMPLE;
    }
}
