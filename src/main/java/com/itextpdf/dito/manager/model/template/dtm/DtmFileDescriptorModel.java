package com.itextpdf.dito.manager.model.template.dtm;

import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionDescriptorModel;

import com.itextpdf.dito.manager.model.template.dtm.datasample.DtmDataSampleDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateDescriptorModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DtmFileDescriptorModel {
    private final String format;
    private final String templateManagerVersion;
    private List<DtmDataCollectionDescriptorModel> dataCollections;
    private List<DtmDataSampleDescriptorModel> dataSamples;
    private Map<String, DtmTemplateDescriptorModel> templates;
    private Map<String, DtmResourceDescriptorModel> resources;

    public DtmFileDescriptorModel(final String format,
                                  final String templateManagerVersion) {
        this.format = format;
        this.templateManagerVersion = templateManagerVersion;
    }

    public String getFormat() {
        return format;
    }

    public String getTemplateManagerVersion() {
        return templateManagerVersion;
    }

    public List<DtmDataCollectionDescriptorModel> getDataCollections() {
        return dataCollections;
    }

    public void setDataCollections(List<DtmDataCollectionDescriptorModel> dataCollections) {
        this.dataCollections = dataCollections;
    }

    public List<DtmDataSampleDescriptorModel> getDataSamples() {
        return dataSamples;
    }

    public void setDataSamples(List<DtmDataSampleDescriptorModel> dataSamples) {
        this.dataSamples = dataSamples;
    }

    public Collection<DtmTemplateDescriptorModel> getTemplates() {
        return Optional.ofNullable(templates).map(Map::values).orElse(Collections.emptyList());
    }

    public void setTemplates(final Collection<DtmTemplateDescriptorModel> templates) {
        this.templates = Optional.ofNullable(templates)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(DtmTemplateDescriptorModel::getId, Function.identity()));
    }

    public Collection<DtmResourceDescriptorModel> getResources() {
        return Optional.ofNullable(resources).map(Map::values).orElse(Collections.emptyList());
    }

    public void setResources(final Collection<DtmResourceDescriptorModel> resources) {
        this.resources = Optional.ofNullable(resources)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(DtmResourceDescriptorModel::getId, Function.identity()));
    }

}
