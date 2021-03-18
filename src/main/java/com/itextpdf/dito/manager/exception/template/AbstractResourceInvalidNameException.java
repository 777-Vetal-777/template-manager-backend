package com.itextpdf.dito.manager.exception.template;

public class AbstractResourceInvalidNameException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = " is invalid. Name should be less than 200 characters long, starts with letter, contains only latin symbols, digits and  '_','()','-' symbols";

	public AbstractResourceInvalidNameException(final String name,final String abstractResourceType) {
		super(buildMessage(name, abstractResourceType));
	}

	private static String buildMessage(final String name, final String abstractResourceType) {
		final StringBuilder result = new StringBuilder(abstractResourceType);
		result.append(" with name: ");
		result.append(name);
		result.append(MESSAGE);
		return result.toString();
	}
}