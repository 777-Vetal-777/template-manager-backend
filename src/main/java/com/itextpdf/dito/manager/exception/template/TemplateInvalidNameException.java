package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class TemplateInvalidNameException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = " is invalid. Name should be less than 200 characters long, starts with letter, contains only latin symbols, digits and  '_','()','-' symbols";

	public TemplateInvalidNameException(final String name) {
		super(buildMessage(name));
	}

	private static String buildMessage(final String name) {
		final StringBuilder result = new StringBuilder(AliasConstants.TEMPLATE);
		result.append(" with name: ");
		result.append(name);
		result.append(MESSAGE);
		return result.toString();
	}
}