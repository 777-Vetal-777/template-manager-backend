package com.itextpdf.dito.manager.exception.workspace;

/**
 * Will be removed in the future releases after the multiple workspaces support will be implemented
 * @deprecated
 */
@Deprecated(since = "0.0.1")
public class OnlyOneWorkspaceAllowedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Only singular workspace is allowed. Multiple workspaces support will be added in the future.";

    public OnlyOneWorkspaceAllowedException() {
        super(MESSAGE);
    }
}
