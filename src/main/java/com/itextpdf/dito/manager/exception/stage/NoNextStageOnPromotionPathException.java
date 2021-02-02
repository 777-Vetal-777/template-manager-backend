package com.itextpdf.dito.manager.exception.stage;

public class NoNextStageOnPromotionPathException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "No further stage on promotion path";
    public NoNextStageOnPromotionPathException() {
        super(MESSAGE);
    }
}
