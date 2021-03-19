package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

import java.io.File;
import java.util.List;

public interface TemplateFileProjectGenerator {
    File generateProjectFolderByTemplate(TemplateFileEntity templateFileEntity, DataSampleFileEntity dataSampleFileEntity);

    File generateZippedProjectByTemplate(TemplateFileEntity templateFileEntity, DataSampleFileEntity dataSampleFileEntity, boolean exportDependencies);

    File generateZippedProjectByTemplate(TemplateFileEntity templateFileEntity, List<DataSampleFileEntity> dataSampleFileEntities, boolean exportDependencies);
}
