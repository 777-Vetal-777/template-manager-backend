package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface TemplateProjectGenerator {
    File generateProjectFolderByTemplate(TemplateEntity templateEntity, DataSampleFileEntity dataSampleFileEntity);

    File generateZippedProjectByTemplate(TemplateEntity templateEntity, DataSampleFileEntity dataSampleFileEntity);

    File generateZippedProjectByTemplate(TemplateEntity templateEntity, List<DataSampleFileEntity> dataSampleFileEntities);
}
