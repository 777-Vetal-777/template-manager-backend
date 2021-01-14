package com.itextpdf.dito.manager.dto.permission;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataCollectionPermissionDTO {
    private String name;
    private String type;
    @JsonProperty("E6_US34_EDIT_DATA_COLLECTION_METADATA")
    private Boolean editDataCollectionMetadata;
    @JsonProperty("E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON")
    private Boolean createNewVersionOfDataCollectionUsingJson;
    @JsonProperty("E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION")
    private Boolean rollBackDataCollection;
    @JsonProperty("E6_US38_DELETE_DATA_COLLECTION")
    private Boolean deleteDataCollection;
    @JsonProperty("E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE")
    private Boolean createNewDataSampleBasedOnJsonFile;
    @JsonProperty("E7_US47_EDIT_SAMPLE_METADATA")
    private Boolean editSampleMetadata;
    @JsonProperty("E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE")
    private Boolean createNewVersionOfDataSample;
    @JsonProperty("E7_US50_DELETE_DATA_SAMPLE")
    private Boolean deleteDataSample;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEditDataCollectionMetadata() {
        return editDataCollectionMetadata;
    }

    public void setEditDataCollectionMetadata(Boolean editDataCollectionMetadata) {
        this.editDataCollectionMetadata = editDataCollectionMetadata;
    }

    public Boolean getCreateNewVersionOfDataCollectionUsingJson() {
        return createNewVersionOfDataCollectionUsingJson;
    }

    public void setCreateNewVersionOfDataCollectionUsingJson(Boolean createNewVersionOfDataCollectionUsingJson) {
        this.createNewVersionOfDataCollectionUsingJson = createNewVersionOfDataCollectionUsingJson;
    }

    public Boolean getRollBackDataCollection() {
        return rollBackDataCollection;
    }

    public void setRollBackDataCollection(Boolean rollBackDataCollection) {
        this.rollBackDataCollection = rollBackDataCollection;
    }

    public Boolean getDeleteDataCollection() {
        return deleteDataCollection;
    }

    public void setDeleteDataCollection(Boolean deleteDataCollection) {
        this.deleteDataCollection = deleteDataCollection;
    }

    public Boolean getCreateNewDataSampleBasedOnJsonFile() {
        return createNewDataSampleBasedOnJsonFile;
    }

    public void setCreateNewDataSampleBasedOnJsonFile(Boolean createNewDataSampleBasedOnJsonFile) {
        this.createNewDataSampleBasedOnJsonFile = createNewDataSampleBasedOnJsonFile;
    }

    public Boolean getEditSampleMetadata() {
        return editSampleMetadata;
    }

    public void setEditSampleMetadata(Boolean editSampleMetadata) {
        this.editSampleMetadata = editSampleMetadata;
    }

    public Boolean getCreateNewVersionOfDataSample() {
        return createNewVersionOfDataSample;
    }

    public void setCreateNewVersionOfDataSample(Boolean createNewVersionOfDataSample) {
        this.createNewVersionOfDataSample = createNewVersionOfDataSample;
    }

    public Boolean getDeleteDataSample() {
        return deleteDataSample;
    }

    public void setDeleteDataSample(Boolean deleteDataSample) {
        this.deleteDataSample = deleteDataSample;
    }
}
