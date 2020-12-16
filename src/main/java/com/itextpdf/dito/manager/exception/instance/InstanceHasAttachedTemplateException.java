package com.itextpdf.dito.manager.exception.instance;

public class InstanceHasAttachedTemplateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "You can't disconnect that instance. It has templates connected.\t-";

    public InstanceHasAttachedTemplateException() {
        super(message);
    }
}
