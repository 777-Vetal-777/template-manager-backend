package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

import java.util.List;

public interface TemplateDeploymentService {
    void promoteOnDefaultStage(TemplateFileEntity templateFileEntity);

    void removeAllVersionsFromDefaultStage(List<TemplateFileEntity> templateVersions);

    TemplateFileEntity promote(String templateName, Long version);

    TemplateFileEntity undeploy(String templateName, Long version);

    StageEntity getNextStage(String templateName, Long version);

    void promoteTemplateToInstance(InstanceEntity instanceEntity, TemplateFileEntity templateFileEntity, boolean isDefaultInstance);
}
