package com.itextpdf.dito.manager.exception.permission;

public class PermissionCantBeAttachedToCustomRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PermissionCantBeAttachedToCustomRoleException(final String roleName) {
        super(buildMessage(roleName));
    }

    private static String buildMessage(final String roleName) {
        return new StringBuilder("Permission ").append(roleName).append(" can't be attached to a custom role.").toString();
    }

}
