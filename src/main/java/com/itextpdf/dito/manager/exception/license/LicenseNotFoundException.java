package com.itextpdf.dito.manager.exception.license;

import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class LicenseNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    @Override
    protected String getResourceAlias() {
        return AliasConstants.LICENSE;
    }
}
