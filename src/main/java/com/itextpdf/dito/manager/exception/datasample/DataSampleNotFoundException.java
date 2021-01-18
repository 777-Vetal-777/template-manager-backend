package com.itextpdf.dito.manager.exception.datasample;

import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class DataSampleNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public DataSampleNotFoundException(String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.DATA_SAMPLE;
    }
}
