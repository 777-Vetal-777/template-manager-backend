package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;

import java.util.List;

public interface TemplateFilePartService {

    List<TemplateFilePartEntity> createTemplatePartEntities(String dataCollectionName, List<TemplatePartModel> templateParts);

    TemplateFilePartEntity updateComposition(TemplateFilePartEntity templateFilePartEntity, TemplateFileEntity composition);

    TemplatePartModel mapFromEntity(TemplateFilePartEntity entity);

}
