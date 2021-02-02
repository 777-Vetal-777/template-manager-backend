package com.itextpdf.dito.manager.exception.instance.deployment;

public class InstanceRegistrationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Unable to register to instance: ";

    public InstanceRegistrationException(final String instanceName) {
        super(buildMessage(instanceName));

    }
    private static String buildMessage(final String instanceName) {
        return new StringBuilder().append(MESSAGE).append(instanceName).toString();
    }
}
