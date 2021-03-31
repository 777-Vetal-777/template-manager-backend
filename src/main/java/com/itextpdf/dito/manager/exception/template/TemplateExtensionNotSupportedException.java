package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class TemplateExtensionNotSupportedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TemplateExtensionNotSupportedException(final String extension) {
        super(buildMessage(extension));
    }

    private static String buildMessage(final String extension) {
        final StringBuilder result = new StringBuilder(AliasConstants.TEMPLATE);
        result.append(" with extension: ");
        result.append(extension);
        result.append(" not supported.");
        return result.toString();
    }
}
