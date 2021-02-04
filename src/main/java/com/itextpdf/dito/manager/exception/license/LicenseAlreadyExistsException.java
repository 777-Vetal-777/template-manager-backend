package com.itextpdf.dito.manager.exception.license;

import com.itextpdf.dito.manager.exception.AbstractResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.AliasConstants;

public class LicenseAlreadyExistsException extends AbstractResourceAlreadyExistsException {
    private static final long serialVersionUID = 1L;
    private final String workspaceName;

    public LicenseAlreadyExistsException(final String workspaceName) {
        super(workspaceName);
        this.workspaceName = workspaceName;
    }
    
	@Override
	public String getMessage() {
		final StringBuilder message = new StringBuilder("Workspace ");
		message.append(workspaceName);
		message.append(" is already has license file");
		return message.toString();
	}
    
    @Override
    protected String getResourceAlias() {
        return AliasConstants.LICENSE;
    }
}