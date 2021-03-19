package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.service.template.TemplateFileProjectGenerator;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class TemplateProjectGeneratorImpl implements TemplateProjectGenerator {

    private final TemplateFileProjectGenerator templateFileProjectGenerator;

    public TemplateProjectGeneratorImpl(final TemplateFileProjectGenerator templateFileProjectGenerator) {
        this.templateFileProjectGenerator = templateFileProjectGenerator;
    }

    @Override
    public File generateProjectFolderByTemplate(final TemplateEntity templateEntity, final DataSampleFileEntity dataSampleFileEntity) {
        return templateFileProjectGenerator.generateProjectFolderByTemplate(templateEntity.getLatestFile(), dataSampleFileEntity);
    }

    @Override
    public File generateZippedProjectByTemplate(final TemplateEntity templateEntity, final DataSampleFileEntity dataSampleFileEntity) {
        return templateFileProjectGenerator.generateZippedProjectByTemplate(templateEntity.getLatestFile(), dataSampleFileEntity, true);
    }

    @Override
    public File generateZippedProjectByTemplate(final TemplateEntity templateEntity, final List<DataSampleFileEntity> dataSampleFileEntities) {
        return templateFileProjectGenerator.generateZippedProjectByTemplate(templateEntity.getLatestFile(), dataSampleFileEntities, true);
    }

    @Override
    public File generateZippedProjectByTemplate(final TemplateEntity templateEntity, final List<DataSampleFileEntity> dataSampleFileEntities, final boolean exportDependencies) {
        return templateFileProjectGenerator.generateZippedProjectByTemplate(templateEntity.getLatestFile(), dataSampleFileEntities, exportDependencies);
    }

}
