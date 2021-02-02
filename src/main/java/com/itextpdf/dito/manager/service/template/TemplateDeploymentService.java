package com.itextpdf.dito.manager.service.template;

import java.io.OutputStream;

public interface TemplateDeploymentService {
    void promoteOnDefaultStage(String templateName);

    void promote(String templateName, Long version);

    void undeploy(String templateName, Long version);
}
