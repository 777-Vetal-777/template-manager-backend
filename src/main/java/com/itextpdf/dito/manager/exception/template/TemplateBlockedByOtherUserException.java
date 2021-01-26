package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class TemplateBlockedByOtherUserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public TemplateBlockedByOtherUserException(final String templateName, final String userName) {
        message = buildMessage(templateName, userName);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String buildMessage(final String templateName, final String userName) {
        return new StringBuilder().append(AliasConstants.TEMPLATE)
                .append(templateName)
                .append(" was blocked by user ")
                .append(userName)
                .toString();
    }
}
