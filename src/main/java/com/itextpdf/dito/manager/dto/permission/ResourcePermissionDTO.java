package com.itextpdf.dito.manager.dto.permission;


public class ResourcePermissionDTO {
    private String name;
    private String type;
    protected Boolean deleteResource;
    protected Boolean createNewVersionResource;
    protected Boolean editResourceMetadata;
    protected Boolean rollBackResource;


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

    public Boolean getDeleteResource() {
        return deleteResource;
    }

    public void setDeleteResource(Boolean deleteResource) {
        this.deleteResource = deleteResource;
    }

    public Boolean getCreateNewVersionResource() {
        return createNewVersionResource;
    }

    public void setCreateNewVersionResource(Boolean createNewVersionResource) {
        this.createNewVersionResource = createNewVersionResource;
    }

    public Boolean getEditResourceMetadata() {
        return editResourceMetadata;
    }

    public void setEditResourceMetadata(Boolean editResourceMetadata) {
        this.editResourceMetadata = editResourceMetadata;
    }

    public Boolean getRollBackResource() {
        return rollBackResource;
    }

    public void setRollBackResource(Boolean rollBackResource) {
        this.rollBackResource = rollBackResource;
    }
}
