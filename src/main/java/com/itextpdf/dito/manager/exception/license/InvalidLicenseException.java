package com.itextpdf.dito.manager.exception.license;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class InvalidLicenseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidLicenseException() {
    	super(AliasConstants.LICENSE + " is not valid.");
    }

}