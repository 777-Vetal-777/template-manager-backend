package com.itextpdf.dito.manager.exception.license;

public class EmptyLicenseFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "License's file couldn't be empty.";

	public EmptyLicenseFileException() {
		super(MESSAGE);
	}

}