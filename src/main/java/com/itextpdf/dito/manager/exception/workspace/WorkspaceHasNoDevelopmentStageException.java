package com.itextpdf.dito.manager.exception.workspace;

public class WorkspaceHasNoDevelopmentStageException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Lowest stage has not met development stage requirements";
    public WorkspaceHasNoDevelopmentStageException() {
        super(MESSAGE);
    }
}
