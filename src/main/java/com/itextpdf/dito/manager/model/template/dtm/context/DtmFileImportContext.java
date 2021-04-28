package com.itextpdf.dito.manager.model.template.dtm.context;

import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.model.template.duplicates.impl.DuplicatesListImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class DtmFileImportContext {

    private final DuplicatesList duplicates;
    private final List<TemplateEntity> templateEntities;
    private final Map<SettingType, Map<String, TemplateImportNameModel>> settings;
    private final String email;
    private final String fileName;
    private final Map<String, Long> templateIdMappingContext = new TreeMap<>();
    private final Map<String, Map<Long, Long>> templateVersionMappingContext = new TreeMap<>();
    private final Map<String, Long> resourceIdMappingContext = new TreeMap<>();
    private final Map<String, Map<Long, Long>> resourceVersionMappingContext = new TreeMap<>();
    private final Map<String, Long> dataCollectionIdMappingContext = new TreeMap<>();
    private final Map<String, Map<Long, Long>> dataCollectionVersionMappingContext = new TreeMap<>();
    private final Map<Long, Long> dataSampleIdMappingContext = new TreeMap<>();
    private final Map<Long, Map<Long, Long>> dataSampleVersionMappingContext = new TreeMap<>();

    public DtmFileImportContext(final Map<SettingType, Map<String, TemplateImportNameModel>> settings,
                                final String email,
                                final String fileName) {
        this.settings = settings;
        this.email = email;
        this.fileName = fileName;
        duplicates = new DuplicatesListImpl();
        templateEntities = new ArrayList<>();
    }

    public void map(final String id, final TemplateEntity entity) {
        templateIdMappingContext.put(id, entity.getId());
    }

    public void map(final String id, final Long version, final TemplateFileEntity templateFileEntity) {
        final Map<Long, Long> versionMapping = templateVersionMappingContext.computeIfAbsent(id, aLong -> new TreeMap<>());
        versionMapping.put(version, templateFileEntity.getVersion());
    }

    public Long getTemplateMapping(final String id) {
        return templateIdMappingContext.get(id);
    }

    public Long getTemplateMapping(final String id, final Long version) {
        return Optional.ofNullable(templateVersionMappingContext.get(id)).map(map -> map.get(version)).orElse(null);
    }

    public void map(final String id, final ResourceEntity entity) {
        resourceIdMappingContext.put(id, entity.getId());
    }

    public void map(final String id, final Long version, final ResourceFileEntity resourceFileEntity) {
        final Map<Long, Long> versionMapping = resourceVersionMappingContext.computeIfAbsent(id, aLong -> new TreeMap<>());
        versionMapping.put(version, resourceFileEntity.getVersion());
    }

    public Long getResourceMapping(final String id) {
        return resourceIdMappingContext.get(id);
    }

    public Long getResourceMapping(final String id, final Long version) {
        return Optional.ofNullable(resourceVersionMappingContext.get(id)).map(map -> map.get(version)).orElse(null);
    }

    public void map(final String id, final DataCollectionEntity entity) {
        dataCollectionIdMappingContext.put(id, entity.getId());
    }

    public void map(final String id, final Long version, final DataCollectionFileEntity collectionFileEntity) {
        final Map<Long, Long> versionMapping = dataCollectionVersionMappingContext.computeIfAbsent(id, aLong -> new TreeMap<>());
        versionMapping.put(version, collectionFileEntity.getVersion());
    }

    public Long getCollectionMapping(final String id) {
        return dataCollectionIdMappingContext.get(id);
    }

    public Long getCollectionMapping(final String id, final Long version) {
        return Optional.ofNullable(dataCollectionVersionMappingContext.get(id)).map(map -> map.get(version)).orElse(null);
    }

    public void map(final Long id, final DataSampleEntity entity) {
        dataSampleIdMappingContext.put(id, entity.getId());
    }

    public void map(final Long id, final Long version, final DataSampleFileEntity collectionFileEntity) {
        final Map<Long, Long> versionMapping = dataSampleVersionMappingContext.computeIfAbsent(id, aLong -> new TreeMap<>());
        versionMapping.put(version, collectionFileEntity.getVersion());
    }

    public Long getSampleMapping(final Long id) {
        return dataSampleIdMappingContext.get(id);
    }

    public Long getSampleMapping(final Long id, final Long version) {
        return Optional.ofNullable(dataSampleVersionMappingContext.get(id)).map(map -> map.get(version)).orElse(null);
    }

    public DuplicatesList getDuplicatesList() {
        return duplicates;
    }

    public DuplicatesList putToDuplicates(SettingType template, String templateName) {
        return duplicates.putToDuplicates(template, templateName);
    }

    public boolean isDuplicatesEmpty() {
        return duplicates.isEmpty();
    }

    public List<TemplateEntity> getTemplateEntities() {
        return Collections.unmodifiableList(templateEntities);
    }

    public boolean add(TemplateEntity templateEntity) {
        return templateEntities.add(templateEntity);
    }

    public Map<String, TemplateImportNameModel> getSettings(final SettingType type) {
        return Optional.ofNullable(settings).map(settings -> settings.get(type)).orElse(Collections.emptyMap());
    }

    public String getEmail() {
        return email;
    }

    public String getFileName() {
        return fileName;
    }
}
