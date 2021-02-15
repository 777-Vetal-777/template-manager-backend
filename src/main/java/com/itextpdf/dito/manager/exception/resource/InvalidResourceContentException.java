package com.itextpdf.dito.manager.exception.resource;

public class InvalidResourceContentException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Resource content is not valid";

    public InvalidResourceContentException() {
        super(MESSAGE);
    }

    public InvalidResourceContentException(Throwable e) {
        super(new StringBuilder(MESSAGE).append(": ").append(e.getMessage()).toString(), e);
    }

}
