package com.itextpdf.dito.manager.filter.version;

import java.util.List;

public class VersionFilter {
    private Long version;
    private String modifiedBy;
    private List<String> modifiedOn;
    private String comment;
    private List<Boolean> deployed;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public List<String> getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(List<String> modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Boolean> getDeployed() {
        return deployed;
    }

    public void setDeployed(List<Boolean> deployed) {
        this.deployed = deployed;
    }
}
