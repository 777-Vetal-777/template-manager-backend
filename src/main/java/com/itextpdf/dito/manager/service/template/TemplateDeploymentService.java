package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

public interface TemplateDeploymentService {
    void promoteOnDefaultStage(TemplateFileEntity templateFileEntity);

    void removeAllVersionsFromDefaultStage(String templateName);

    TemplateFileEntity promote(String templateName, Long version);

    TemplateFileEntity undeploy(String templateName, Long version);

    StageEntity getNextStage(String templateName, Long version);
}
