package com.itextpdf.dito.manager.exception.resource;

public class ResourceHasDependenciesException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    private final String message = "Recource has outbound dependencies";

    @Override
    public String getMessage() {
        return message;
    }
	
	
}
