package com.itextpdf.dito.manager.dto.datacollection;

import java.util.Date;

public class DataCollectionVersionDTO {
    private Long version;
    private String modifiedBy;
    private Date modifiedOn;
    private String comment;
    boolean deploymentStatus;

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

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(boolean deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }
}
