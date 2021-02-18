package com.itextpdf.dito.manager.model.resource;

public interface ResourcePermissionModel {
    String getName();
    String getType();
    String getResourceType();
    Boolean getE8_US55_EDIT_RESOURCE_METADATA_IMAGE();
    Boolean getE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE();
    Boolean getE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE();
    Boolean getE8_US66_DELETE_RESOURCE_IMAGE();
    Boolean getE8_US66_1_DELETE_RESOURCE_FONT();
    Boolean getE8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT();
    Boolean getE8_US58_EDIT_RESOURCE_METADATA_FONT();
    Boolean getE8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT();
    Boolean getE8_US66_2_DELETE_RESOURCE_STYLESHEET();
    Boolean getE8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET();
    Boolean getE8_US61_EDIT_RESOURCE_METADATA_STYLESHEET();
    Boolean getE8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET();
}