package com.itextpdf.dito.manager.exception.user;

public class UserInvalidNameException  extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = " First name and last name should be less than 200 characters long and contains only letters";

    public UserInvalidNameException(final String name) {
        super(buildMessage(name));
    }

    private static String buildMessage(final String name) {
        final StringBuilder result = new StringBuilder("User name doesn't match name requirements: ");
        result.append(name);
        result.append(MESSAGE);
        return result.toString();
    }
}