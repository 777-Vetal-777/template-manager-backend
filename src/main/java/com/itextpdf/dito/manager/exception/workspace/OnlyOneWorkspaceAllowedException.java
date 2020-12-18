package com.itextpdf.dito.manager.exception.workspace;

/**
 * Will be removed in the future releases.
 */
@Deprecated
public class OnlyOneWorkspaceAllowedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "Only singular workspace is allowed. Multiple workspaces support will be added in the future.";

    public OnlyOneWorkspaceAllowedException() {
        super(message);
    }
}
