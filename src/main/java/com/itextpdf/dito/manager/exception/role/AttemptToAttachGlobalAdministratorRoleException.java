package com.itextpdf.dito.manager.exception.role;

public class AttemptToAttachGlobalAdministratorRoleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "You can't attach 'GLOBAL_ADMINISTRATOR' role.";

    public AttemptToAttachGlobalAdministratorRoleException() {
        super(MESSAGE);
    }
}
