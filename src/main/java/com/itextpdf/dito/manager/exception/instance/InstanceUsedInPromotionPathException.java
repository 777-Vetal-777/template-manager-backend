package com.itextpdf.dito.manager.exception.instance;

public class InstanceUsedInPromotionPathException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String message = "You can't disconnect that instance. It's a part of the promotion path.";

    public InstanceUsedInPromotionPathException() {
        super(message);
    }
}
