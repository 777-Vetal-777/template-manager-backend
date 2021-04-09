package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AliasConstants;
import liquibase.pro.packaged.er;

public class TemplateCannotBePromotedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TemplateCannotBePromotedException(final String templateName, final String errorMessage) {
        super(buildMessage(templateName, errorMessage));
    }

    private static String buildMessage(final String templateName, final String errorMessage) {
        return new StringBuilder(AliasConstants.TEMPLATE)
                .append(" with name: ")
                .append(templateName)
                .append(" can be created, but cannot be promoted. Error message: ")
                .append(errorMessage).toString();
    }
}