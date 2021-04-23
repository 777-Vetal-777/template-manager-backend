package com.itextpdf.dito.manager.model.template.dtm.context;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class DtmFileExportContext extends AbstractDtmFileContext {
    private final boolean exportDependencies;
    private final TemplateEntity templateEntity;
    private final Collection<TemplateFileEntity> templateFileEntities = new HashSet<>();

    public DtmFileExportContext(final TemplateEntity templateEntity, boolean exportDependencies) {
        this.exportDependencies = exportDependencies;
        this.templateEntity = templateEntity;
        this.templateFileEntities.addAll(templateEntity.getFiles());
    }

    public DtmFileExportContext(final TemplateFileEntity fileEntity, boolean exportDependencies) {
        this.exportDependencies = exportDependencies;
        this.templateEntity = fileEntity.getTemplate();
        this.templateFileEntities.add(fileEntity);
    }

    public boolean isExportDependencies() {
        return exportDependencies;
    }

    public TemplateEntity getTemplateEntity() {
        return templateEntity;
    }

    public Collection<TemplateFileEntity> getTemplateFileEntities() {
        return Collections.unmodifiableCollection(templateFileEntities);
    }

    public Collection<TemplateFileEntity> addAll(final Collection<TemplateFileEntity> templateFileEntities) {
        this.templateFileEntities.addAll(templateFileEntities);
        return getTemplateFileEntities();
    }

}
