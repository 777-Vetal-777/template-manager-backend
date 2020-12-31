package com.itextpdf.dito.manager.exception.workspace;

public class WorkspaceHasNoDevelopmentStageException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "Workspace has no development stage at all.";

    public WorkspaceHasNoDevelopmentStageException() {
        super(message);
    }

    public WorkspaceHasNoDevelopmentStageException(String message) {
        super(message);
    }
}
