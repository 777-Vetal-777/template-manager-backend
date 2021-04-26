package com.itextpdf.dito.manager.exception.role;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class RoleHasConnectedUsersException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RoleHasConnectedUsersException(final String roleName) {
        super(buildMessage(roleName));
    }

    private static String buildMessage(final String roleName) {
        return new StringBuilder().append(AliasConstants.ROLE)
                .append(" ")
                .append(roleName)
                .append(" has connected users")
                .toString();
    }
}
