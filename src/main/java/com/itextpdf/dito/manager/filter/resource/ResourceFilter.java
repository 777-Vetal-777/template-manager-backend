package com.itextpdf.dito.manager.filter.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;

import java.util.List;

public class ResourceFilter {
    private String name;
    private List<ResourceTypeEnum> type;
    private String modifiedBy;
    private List<String> modifiedOn;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ResourceTypeEnum> getType() {
        return type;
    }

    public void setType(List<ResourceTypeEnum> type) {
        this.type = type;
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
}
