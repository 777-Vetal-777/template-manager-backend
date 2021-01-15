package com.itextpdf.dito.manager.dto.permission;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourcePermissionDTO {
    private String name;
    private String type;
    @JsonProperty("E8_US66_DELETE_RESOURCE_IMAGE")
    private Boolean deleteResourceImage;
    @JsonProperty("E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE")
    private Boolean createNewVersionResourceImage;
    @JsonProperty("E8_US55_EDIT_RESOURCE_METADATA_IMAGE")
    private Boolean editResourceMetadataImage;
    @JsonProperty("E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE")
    private Boolean rollBackResourceImage;


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

    public Boolean getDeleteResourceImage() {
        return deleteResourceImage;
    }

    public void setDeleteResourceImage(Boolean deleteResourceImage) {
        this.deleteResourceImage = deleteResourceImage;
    }

    public Boolean getCreateNewVersionResourceImage() {
        return createNewVersionResourceImage;
    }

    public void setCreateNewVersionResourceImage(Boolean createNewVersionResourceImage) {
        this.createNewVersionResourceImage = createNewVersionResourceImage;
    }

    public Boolean getEditResourceMetadataImage() {
        return editResourceMetadataImage;
    }

    public void setEditResourceMetadataImage(Boolean editResourceMetadataImage) {
        this.editResourceMetadataImage = editResourceMetadataImage;
    }

    public Boolean getRollBackResourceImage() {
        return rollBackResourceImage;
    }

    public void setRollBackResourceImage(Boolean rollBackResourceImage) {
        this.rollBackResourceImage = rollBackResourceImage;
    }
}
