package com.itextpdf.dito.manager.dto.permission;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourcePermissionImageDTO extends ResourcePermissionDTO{

    @Override
    @JsonProperty("E8_US66_DELETE_RESOURCE_IMAGE")
    public void setDeleteResource(Boolean deleteResource) {
        super.setDeleteResource(deleteResource);
    }

    @Override
    @JsonProperty("E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE")
    public void setCreateNewVersionResource(Boolean createNewVersionResource) {
        super.setCreateNewVersionResource(createNewVersionResource);
    }

    @Override
    @JsonProperty("E8_US55_EDIT_RESOURCE_METADATA_IMAGE")
    public void setEditResourceMetadata(Boolean editResourceMetadata) {
        super.setEditResourceMetadata(editResourceMetadata);
    }

    @Override
    @JsonProperty("E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE")
    public void setRollBackResource(Boolean rollBackResource) {
        super.setRollBackResource(rollBackResource);
    }
}