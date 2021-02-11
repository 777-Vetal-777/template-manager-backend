package com.itextpdf.dito.manager.exception.resource;

public class InvalidResourceContentException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "Resource content is not valid";

    public InvalidResourceContentException() {
        super(message);
    }

    public InvalidResourceContentException(Throwable e) {
        super(message, e);
    }

}
