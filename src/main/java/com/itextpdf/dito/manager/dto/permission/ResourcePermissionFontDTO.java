package com.itextpdf.dito.manager.dto.permission;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourcePermissionFontDTO extends ResourcePermissionDTO{

    @Override
    @JsonProperty("E8_US66_1_DELETE_RESOURCE_FONT")
    public void setDeleteResource(Boolean deleteResource) {
        super.setDeleteResource(deleteResource);
    }

    @Override
    @JsonProperty("E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT")
    public void setCreateNewVersionResource(Boolean createNewVersionResource) {
        super.setCreateNewVersionResource(createNewVersionResource);
    }

    @Override
    @JsonProperty("E8_US58_EDIT_RESOURCE_METADATA_FONT")
    public void setEditResourceMetadata(Boolean editResourceMetadata) {
        super.setEditResourceMetadata(editResourceMetadata);
    }

    @Override
    @JsonProperty("E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT")
    public void setRollBackResource(Boolean rollBackResource) {
        super.setRollBackResource(rollBackResource);
    }
}