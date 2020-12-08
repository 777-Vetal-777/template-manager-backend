package com.itextpdf.dito.manager.exception.user;

import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.AbstractResourceNotFoundException;

public class UserNotFoundException extends AbstractResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String id) {
        super(id);
    }

    public UserNotFoundException() {
        super();
    }

    @Override
    protected String getResourceAlias() {
        return AliasConstants.USER;
    }
}

