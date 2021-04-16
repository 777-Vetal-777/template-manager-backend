package com.itextpdf.dito.manager.component.mapper.template.dtm.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class LocalPathMapperImpl implements LocalPathMapper {
    private static final String TEMPLATE_PATH = "templates";
    private static final String RESOURCE_PATH = "resources";
    private static final String COLLECTION_PATH = "data_collections";
    private static final String SAMPLE_PATH = "data_samples";

    @Override
    public String getTemplateBasePath() {
        return TEMPLATE_PATH;
    }

    @Override
    public String getResourceBasePath() {
        return RESOURCE_PATH;
    }

    @Override
    public String getDataCollectionBasePath() {
        return COLLECTION_PATH;
    }

    @Override
    public String getDataSampleBasePath() {
        return SAMPLE_PATH;
    }

    @Override
    public String getLocalPath(final TemplateFileEntity entity) {
        return new StringBuilder(TEMPLATE_PATH)
                .append("/")
                .append(entity.getUuid())
                .append(".html")
                .toString();
    }

    @Override
    public String getLocalPath(final ResourceFileEntity entity) {
        final String extension = FilenameUtils.getExtension(entity.getFileName());
        return new StringBuilder(RESOURCE_PATH)
                .append("/")
                .append(entity.getUuid())
                .append(StringUtils.isEmpty(extension) ? "" : ".".concat(extension))
                .toString();
    }

    @Override
    public String getLocalPath(final DataCollectionFileEntity entity) {
        final StringBuilder result = new StringBuilder(COLLECTION_PATH)
                .append("/")
                .append(entity.getDataCollection().getUuid())
                .append("-")
                .append(entity.getVersion().toString())
                .append(".json");
        return result.toString();
    }

    @Override
    public String getLocalPath(final DataSampleFileEntity entity) {
        final String extension = FilenameUtils.getExtension(entity.getFileName());
        return new StringBuilder(SAMPLE_PATH)
                .append("/")
                .append(entity.getDataSample().getUuid())
                .append(entity.getVersion().toString())
                .append(StringUtils.isEmpty(extension) ? "" : ".".concat(extension))
                .toString();
    }
}
