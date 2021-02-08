package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.StageEntity;

public interface TemplateDeploymentService {
    void promoteOnDefaultStage(String templateName);

    void removeAllVersionsFromDefaultStage(String templateName);

    void promote(String templateName, Long version);

    void undeploy(String templateName, Long version);

    StageEntity getNextStage(String templateName, Long version);
}
