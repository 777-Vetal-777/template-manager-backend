package com.itextpdf.dito.manager.model.resource;

public interface ResourcePermissionModel {
    String getName();
    String getType();
    Boolean getE8_US55_EDIT_RESOURCE_METADATA_IMAGE();
    Boolean getE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE();
    Boolean getE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE();
    Boolean getE8_US66_DELETE_RESOURCE_IMAGE();
}
