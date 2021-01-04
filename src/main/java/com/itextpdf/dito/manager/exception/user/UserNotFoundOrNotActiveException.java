package com.itextpdf.dito.manager.exception.user;


import com.itextpdf.dito.manager.exception.AliasConstants;

public class UserNotFoundOrNotActiveException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String message;

    public UserNotFoundOrNotActiveException(final String email) {
        message = buildMessage(email);
    }

    @Override
    public String getMessage() {
        return message;
    }
    private String buildMessage(final String id) {
        final StringBuilder result = new StringBuilder();
        result.append(AliasConstants.USER);
        result.append(" with id: ");
        result.append(id);
        result.append(" is not found or not active.");
        return result.toString();
    }
}
