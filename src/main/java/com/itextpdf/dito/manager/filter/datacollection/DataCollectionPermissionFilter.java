package com.itextpdf.dito.manager.filter.datacollection;

import java.util.List;

public class DataCollectionPermissionFilter {

    private List<String> name;
    private List<Boolean> editDataCollectionMetadata;
    private List<Boolean> createNewVersionOfDataCollection;
    private List<Boolean> rollbackOfTheDataCollection;
    private List<Boolean> deleteDataCollection;
    private List<Boolean> createNewDataSample;
    private List<Boolean> editSampleMetadata;
    private List<Boolean> createNewVersionOfDataSample;
    private List<Boolean> deleteDataSample;

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<Boolean> getEditDataCollectionMetadata() {
        return editDataCollectionMetadata;
    }

    public void setE6_US34_EDIT_DATA_COLLECTION_METADATA(List<Boolean> editDataCollectionMetadata) {
        this.editDataCollectionMetadata = editDataCollectionMetadata;
    }

    public List<Boolean> getCreateNewVersionOfDataCollection() {
        return createNewVersionOfDataCollection;
    }

    public void setE6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON(List<Boolean> createNewVersionOfDataCollection) {
        this.createNewVersionOfDataCollection = createNewVersionOfDataCollection;
    }

    public List<Boolean> getRollbackOfTheDataCollection() {
        return rollbackOfTheDataCollection;
    }

    public void setE6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION(List<Boolean> rollbackOfTheDataCollection) {
        this.rollbackOfTheDataCollection = rollbackOfTheDataCollection;
    }

    public List<Boolean> getDeleteDataCollection() {
        return deleteDataCollection;
    }

    public void setE6_US38_DELETE_DATA_COLLECTION(List<Boolean> deleteDataCollection) {
        this.deleteDataCollection = deleteDataCollection;
    }

    public List<Boolean> getCreateNewDataSample() {
        return createNewDataSample;
    }

    public void setE7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE(List<Boolean> createNewDataSample) {
        this.createNewDataSample = createNewDataSample;
    }

    public List<Boolean> getEditSampleMetadata() {
        return editSampleMetadata;
    }

    public void setE7_US47_EDIT_SAMPLE_METADATA(List<Boolean> editSampleMetadata) {
        this.editSampleMetadata = editSampleMetadata;
    }

    public List<Boolean> getCreateNewVersionOfDataSample() {
        return createNewVersionOfDataSample;
    }

    public void setE7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE(List<Boolean> createNewVersionOfDataSample) {
        this.createNewVersionOfDataSample = createNewVersionOfDataSample;
    }

    public List<Boolean> getDeleteDataSample() {
        return deleteDataSample;
    }

    public void setE7_US50_DELETE_DATA_SAMPLE(List<Boolean> deleteDataSample) {
        this.deleteDataSample = deleteDataSample;
    }
}
