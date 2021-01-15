package com.itextpdf.dito.manager.filter.resource;


import java.util.List;


public class ResourcePermissionFilter {
    private List<String> roleName;
    private List<Boolean> E8_US55_EDIT_RESOURCE_METADATA_IMAGE;
    private List<Boolean> E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE;
    private List<Boolean> E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE;
    private List<Boolean> E8_US66_DELETE_RESOURCE_IMAGE;

    public ResourcePermissionFilter(List<String> roleName, List<Boolean> e8_US55_EDIT_RESOURCE_METADATA_IMAGE,
                                    List<Boolean> e8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE, List<Boolean> e8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE,
                                    List<Boolean> e8_US66_DELETE_RESOURCE_IMAGE) {
        this.roleName = roleName;
        E8_US55_EDIT_RESOURCE_METADATA_IMAGE = e8_US55_EDIT_RESOURCE_METADATA_IMAGE;
        E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE = e8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE;
        E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE = e8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE;
        E8_US66_DELETE_RESOURCE_IMAGE = e8_US66_DELETE_RESOURCE_IMAGE;
    }

    public List<String> getRoleName() {
        return roleName;
    }

    public void setRoleName(List<String> roleName) {
        this.roleName = roleName;
    }

    public List<Boolean> getE8_US55_EDIT_RESOURCE_METADATA_IMAGE() {
        return E8_US55_EDIT_RESOURCE_METADATA_IMAGE;
    }

    public void setE8_US55_EDIT_RESOURCE_METADATA_IMAGE(List<Boolean> e8_US55_EDIT_RESOURCE_METADATA_IMAGE) {
        E8_US55_EDIT_RESOURCE_METADATA_IMAGE = e8_US55_EDIT_RESOURCE_METADATA_IMAGE;
    }

    public List<Boolean> getE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE() {
        return E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE;
    }

    public void setE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE(List<Boolean> e8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE) {
        E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE = e8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE;
    }

    public List<Boolean> getE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE() {
        return E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE;
    }

    public void setE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE(List<Boolean> e8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE) {
        E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE = e8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE;
    }

    public List<Boolean> getE8_US66_DELETE_RESOURCE_IMAGE() {
        return E8_US66_DELETE_RESOURCE_IMAGE;
    }

    public void setE8_US66_DELETE_RESOURCE_IMAGE(List<Boolean> e8_US66_DELETE_RESOURCE_IMAGE) {
        E8_US66_DELETE_RESOURCE_IMAGE = e8_US66_DELETE_RESOURCE_IMAGE;
    }
}
