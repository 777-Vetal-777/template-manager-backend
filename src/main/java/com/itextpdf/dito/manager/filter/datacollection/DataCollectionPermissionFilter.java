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

    public void setEditDataCollectionMetadata(List<Boolean> editDataCollectionMetadata) {
        this.editDataCollectionMetadata = editDataCollectionMetadata;
    }

    public List<Boolean> getCreateNewVersionOfDataCollection() {
        return createNewVersionOfDataCollection;
    }

    public void setCreateNewVersionOfDataCollection(List<Boolean> createNewVersionOfDataCollection) {
        this.createNewVersionOfDataCollection = createNewVersionOfDataCollection;
    }

    public List<Boolean> getRollbackOfTheDataCollection() {
        return rollbackOfTheDataCollection;
    }

    public void setRollbackOfTheDataCollection(List<Boolean> rollbackOfTheDataCollection) {
        this.rollbackOfTheDataCollection = rollbackOfTheDataCollection;
    }

    public List<Boolean> getDeleteDataCollection() {
        return deleteDataCollection;
    }

    public void setDeleteDataCollection(List<Boolean> deleteDataCollection) {
        this.deleteDataCollection = deleteDataCollection;
    }

    public List<Boolean> getCreateNewDataSample() {
        return createNewDataSample;
    }

    public void setCreateNewDataSample(List<Boolean> createNewDataSample) {
        this.createNewDataSample = createNewDataSample;
    }

    public List<Boolean> getEditSampleMetadata() {
        return editSampleMetadata;
    }

    public void setEditSampleMetadata(List<Boolean> editSampleMetadata) {
        this.editSampleMetadata = editSampleMetadata;
    }

    public List<Boolean> getCreateNewVersionOfDataSample() {
        return createNewVersionOfDataSample;
    }

    public void setCreateNewVersionOfDataSample(List<Boolean> createNewVersionOfDataSample) {
        this.createNewVersionOfDataSample = createNewVersionOfDataSample;
    }

    public List<Boolean> getDeleteDataSample() {
        return deleteDataSample;
    }

    public void setDeleteDataSample(List<Boolean> deleteDataSample) {
        this.deleteDataSample = deleteDataSample;
    }
}
