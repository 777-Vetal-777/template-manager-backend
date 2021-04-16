package com.itextpdf.dito.manager.model.template.dtm;

public abstract class AbstractDtmItemVersionDescriptorModel {
    private Long version;
    private String comment;
    private String localPath;

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

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public String toString() {
        return "AbstractDtmItemVersionDescriptorModel{" +
                "version=" + version +
                ", comment='" + comment + '\'' +
                ", localPath='" + localPath + '\'' +
                '}';
    }
}
