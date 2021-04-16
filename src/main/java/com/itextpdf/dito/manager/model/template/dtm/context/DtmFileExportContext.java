package com.itextpdf.dito.manager.model.template.dtm.context;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class DtmFileExportContext {
    private final boolean exportDependencies;
    private final TemplateEntity templateEntity;
    private final Collection<TemplateFileEntity> templateFileEntities = new HashSet<>();
    private final Map<Long, Long> templateIdMappingContext = new TreeMap<>();
    private final Map<Long, Map<Long, Long>> templateVersionMappingContext = new TreeMap<>();
    private final Map<Long, Long> resourceIdMappingContext = new TreeMap<>();
    private final Map<Long, Map<Long, Long>> resourceVersionMappingContext = new TreeMap<>();
    private final Map<Long, Long> dataCollectionIdMappingContext = new TreeMap<>();
    private final Map<Long, Map<Long, Long>> dataCollectionVersionMappingContext = new TreeMap<>();
    private final Map<Long, Long> dataSampleIdMappingContext = new TreeMap<>();
    private final Map<Long, Map<Long, Long>> dataSampleVersionMappingContext = new TreeMap<>();

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

    public Collection<TemplateFileEntity> retainAll(final Collection<TemplateFileEntity> templateFileEntities) {
        this.templateFileEntities.retainAll(templateFileEntities);
        return getTemplateFileEntities();
    }

    public void map(final TemplateEntity entity, final Long newId) {
        templateIdMappingContext.put(entity.getId(), newId);
    }

    public void map(final DataSampleEntity entity, final Long newId) {
        dataSampleIdMappingContext.put(entity.getId(), newId);
    }

    public Long getMapping(final TemplateEntity entity) {
        return templateIdMappingContext.get(entity.getId());
    }

    public Long getMapping(final TemplateFileEntity templateFileEntity) {
        return Optional.ofNullable(templateVersionMappingContext.get(templateFileEntity.getTemplate().getId()))
                .map(map -> map.get(templateFileEntity.getVersion()))
                .orElse(null);
    }

    public void map(final TemplateFileEntity templateFileEntity, final Long newVersion) {
        final Map<Long, Long> versionMapping = templateVersionMappingContext.computeIfAbsent(templateFileEntity.getTemplate().getId(), aLong -> new TreeMap<>());
        versionMapping.put(templateFileEntity.getVersion(), newVersion);
    }

    public void map(final DataSampleFileEntity dataSampleFileEntity, final Long newVersion) {
        final Map<Long, Long> versionMapping = dataSampleVersionMappingContext.computeIfAbsent(dataSampleFileEntity.getDataSample().getId(), aLong -> new TreeMap<>());
        versionMapping.put(dataSampleFileEntity.getVersion(), newVersion);
    }

    public void map(final ResourceEntity entity, final Long newId) {
        resourceIdMappingContext.put(entity.getId(), newId);
    }

    public void map(final ResourceFileEntity resourceFileEntity, final Long newVersion) {
        final Map<Long, Long> versionMapping = resourceVersionMappingContext.computeIfAbsent(resourceFileEntity.getResource().getId(), aLong -> new TreeMap<>());
        versionMapping.put(resourceFileEntity.getVersion(), newVersion);
    }

    public Long getMapping(final ResourceEntity entity) {
        return resourceIdMappingContext.get(entity.getId());
    }

    public Long getMapping(final DataSampleEntity entity) {
        return dataSampleIdMappingContext.get(entity.getId());
    }

    public Long getMapping(final ResourceFileEntity resourceFileEntity) {
        return Optional.ofNullable(resourceVersionMappingContext.get(resourceFileEntity.getResource().getId()))
                .map(map -> map.get(resourceFileEntity.getVersion()))
                .orElse(null);
    }

    public Long getMapping(final DataSampleFileEntity dataSampleFileEntity) {
        return Optional.ofNullable(dataSampleVersionMappingContext.get(dataSampleFileEntity.getDataSample().getId()))
                .map(map -> map.get(dataSampleFileEntity.getVersion()))
                .orElse(null);
    }

    public void map(final DataCollectionEntity entity, final Long newId) {
        dataCollectionIdMappingContext.put(entity.getId(), newId);
    }

    public void map(final DataCollectionFileEntity fileEntity, final Long newVersion) {
        final Map<Long, Long> versionMapping = dataCollectionVersionMappingContext.computeIfAbsent(fileEntity.getDataCollection().getId(), aLong -> new TreeMap<>());
        versionMapping.put(fileEntity.getVersion(), newVersion);
    }

    public Long getMapping(final DataCollectionEntity entity) {
        return dataCollectionIdMappingContext.get(entity.getId());
    }

    public Long getMapping(final DataCollectionFileEntity fileEntity) {
        return Optional.ofNullable(dataCollectionVersionMappingContext.get(fileEntity.getDataCollection().getId()))
                .map(map -> map.get(fileEntity.getVersion()))
                .orElse(null);
    }

}
