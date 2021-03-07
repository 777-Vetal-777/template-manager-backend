package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;


public interface TemplateRefreshLinksService {
    void updateResourceLinksInTemplates(ResourceEntity resourceEntity, String newName);

    void updateTemplateLinksInTemplates(TemplateEntity templateEntity, String newName);
}
