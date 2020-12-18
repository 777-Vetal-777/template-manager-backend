package com.itextpdf.dito.manager.exception.role;

public class AttemptToAttachGlobalAdministratorRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "You can't attach 'GLOBAL_ADMINISTRATOR' role.";

    @Override
    public String getMessage() {
        return message;
    }
}
