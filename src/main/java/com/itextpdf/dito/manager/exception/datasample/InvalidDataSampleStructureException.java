package com.itextpdf.dito.manager.exception.datasample;


public class InvalidDataSampleStructureException extends RuntimeException{
	   private static final long serialVersionUID = 1L;

	    private static final String message = "Please check the structure before confirmation";

	    @Override
	    public String getMessage() {
	        return message;
	    }
}
