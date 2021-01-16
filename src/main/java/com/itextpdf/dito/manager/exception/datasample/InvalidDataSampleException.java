package com.itextpdf.dito.manager.exception.datasample;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class InvalidDataSampleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = AliasConstants.DATA_SAMPLE + " is not valid.";

    @Override
    public String getMessage() {
        return message;
    }
}
