package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;

import java.util.List;

public interface TemplateFilePartService {

    List<TemplateFilePartEntity> createTemplatePartEntities(String dataCollectionName, List<TemplatePartDTO> templatePartDTOs);

    TemplateFilePartEntity updateComposition(TemplateFilePartEntity templateFilePartEntity, TemplateFileEntity composition);

    TemplateFilePartEntity updatePart(TemplateFilePartEntity templateFilePartEntity, TemplateFileEntity part);

}
