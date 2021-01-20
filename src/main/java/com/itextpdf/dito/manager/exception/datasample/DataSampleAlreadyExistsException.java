package com.itextpdf.dito.manager.exception.datasample;

import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class DataSampleAlreadyExistsException extends AbstractResourceAlreadyExistsException {
    private static final long serialVersionUID = 1L;

    public DataSampleAlreadyExistsException(final String id) {
        super(id);
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.DATA_SAMPLE;
    }
}