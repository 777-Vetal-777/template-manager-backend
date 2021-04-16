package com.itextpdf.dito.manager.model.template.dtm.datacollection;

import java.util.List;

public class DtmDataCollectionVersionDescriptorModel {
    private Long version;
    private String comment;
    private String fileName;
    private String localPath;
    private List<DtmDataCollectionUsedInDescriptorModel> usedIn;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public List<DtmDataCollectionUsedInDescriptorModel> getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(List<DtmDataCollectionUsedInDescriptorModel> usedIn) {
        this.usedIn = usedIn;
    }
}
