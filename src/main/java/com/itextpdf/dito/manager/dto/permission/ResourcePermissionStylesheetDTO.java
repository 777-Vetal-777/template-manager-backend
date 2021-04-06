package com.itextpdf.dito.manager.dto.permission;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourcePermissionStylesheetDTO extends ResourcePermissionDTO{

    @Override
    @JsonProperty("E8_US66_2_DELETE_RESOURCE_STYLESHEET")
    public void setDeleteResource(Boolean deleteResource) {
        super.setDeleteResource(deleteResource);
    }

    @Override
    @JsonProperty("E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET")
    public void setCreateNewVersionResource(Boolean createNewVersionResource) {
        super.setCreateNewVersionResource(createNewVersionResource);
    }

    @Override
    @JsonProperty("E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET")
    public void setEditResourceMetadata(Boolean editResourceMetadata) {
        super.setEditResourceMetadata(editResourceMetadata);
    }

    @Override
    @JsonProperty("E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET")
    public void setRollBackResource(Boolean rollBackResource) {
        super.setRollBackResource(rollBackResource);
    }
}