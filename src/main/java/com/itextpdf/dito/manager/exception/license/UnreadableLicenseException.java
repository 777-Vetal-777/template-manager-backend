package com.itextpdf.dito.manager.exception.license;

import com.itextpdf.dito.manager.exception.AliasConstants;

public class UnreadableLicenseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnreadableLicenseException(String name) {
		super(buildMessage(name));
	}

	private static String buildMessage(final String name) {
		final StringBuilder message = new StringBuilder(AliasConstants.LICENSE);
		message.append(" with name ");
		message.append(name);
		message.append("can't be read.");
		return message.toString();
	}

}