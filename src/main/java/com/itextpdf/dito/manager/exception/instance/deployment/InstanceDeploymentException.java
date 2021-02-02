package com.itextpdf.dito.manager.exception.instance.deployment;

public class InstanceDeploymentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Unable to deploy template to instance: ";

    public InstanceDeploymentException(final String instanceName) {
        super(buildMessage(instanceName));
    }

    private static String buildMessage(final String instanceName) {
        return new StringBuilder().append(MESSAGE).append(instanceName).toString();
    }
}
