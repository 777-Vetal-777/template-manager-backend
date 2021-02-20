package com.itextpdf.dito.manager.dto.template;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class TemplateImportRequestDTO {
    @NotBlank
    private String name;

    @JsonProperty(value = "data_collections")
    private List<DataCollectionNameDTO> dataCollections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataCollectionNameDTO> getDataCollections() {
        return dataCollections;
    }

    public void setDataCollections(List<DataCollectionNameDTO> dataCollections) {
        this.dataCollections = dataCollections;
    }
}
