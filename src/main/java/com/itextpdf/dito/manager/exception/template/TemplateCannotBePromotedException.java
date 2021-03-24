package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class TemplateCannotBePromotedException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public TemplateCannotBePromotedException(final String templateName) {
        super(buildMessage(templateName));
    }

    private static String buildMessage(final String templateName) {
        return new StringBuilder(AliasConstants.TEMPLATE)
                .append(" with name: ")
                .append(templateName)
                .append(" can be created, but cannot be promoted").toString();
    }
}