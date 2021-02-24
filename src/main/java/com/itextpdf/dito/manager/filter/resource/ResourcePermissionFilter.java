package com.itextpdf.dito.manager.filter.resource;


import java.util.List;


public class ResourcePermissionFilter {
    private List<String> name;
    private List<Boolean> editResourceMetadataImage;
    private List<Boolean> createNewVersionResourceImage;
    private List<Boolean> rollBackResourceImage;
    private List<Boolean> deleteResourceImage;
    private List<Boolean> editResourceMetadataFont;
    private List<Boolean> createNewVersionResourceFont;
    private List<Boolean> rollBackResourceFont;
    private List<Boolean> deleteResourceFont;
    private List<Boolean> editResourceMetadataStylesheet;
    private List<Boolean> createNewVersionResourceStylesheet;
    private List<Boolean> rollBackResourceStylesheet;
    private List<Boolean> deleteResourceStylesheet;

    public void setE8_US55_EDIT_RESOURCE_METADATA_IMAGE(List<Boolean> editResourceMetadataImage) {
        this.editResourceMetadataImage = editResourceMetadataImage;
    }

    public void setE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE(List<Boolean> createNewVersionResourceImage) {
        this.createNewVersionResourceImage = createNewVersionResourceImage;
    }

    public void setE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE(List<Boolean> rollBackResourceImage) {
        this.rollBackResourceImage = rollBackResourceImage;
    }

    public void setE8_US66_DELETE_RESOURCE_IMAGE(List<Boolean> deleteResourceImage) {
        this.deleteResourceImage = deleteResourceImage;
    }

    public void setE8_US58_EDIT_RESOURCE_METADATA_FONT(List<Boolean> editResourceMetadataFont) {
        this.editResourceMetadataFont = editResourceMetadataFont;
    }

    public void setE8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT(List<Boolean> createNewVersionResourceFont) {
        this.createNewVersionResourceFont = createNewVersionResourceFont;
    }

    public void setE8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT(List<Boolean> rollBackResourceFont) {
        this.rollBackResourceFont = rollBackResourceFont;
    }

    public void setE8_US66_1_DELETE_RESOURCE_FONT(List<Boolean> deleteResourceFont) {
        this.deleteResourceFont = deleteResourceFont;
    }

    public void setE8_US61_EDIT_RESOURCE_METADATA_STYLESHEET(List<Boolean> editResourceMetadataStylesheet) {
        this.editResourceMetadataStylesheet = editResourceMetadataStylesheet;
    }

    public void setE8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET(List<Boolean> createNewVersionResourceStylesheet) {
        this.createNewVersionResourceStylesheet = createNewVersionResourceStylesheet;
    }

    public void setE8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET(List<Boolean> rollBackResourceStylesheet) {
        this.rollBackResourceStylesheet = rollBackResourceStylesheet;
    }

    public void setE8_US66_2_DELETE_RESOURCE_STYLESHEET(List<Boolean> deleteResourceStylesheet) {
        this.deleteResourceStylesheet = deleteResourceStylesheet;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getName() {
        return name;
    }

    public List<Boolean> getEditResourceMetadataImage() {
        return editResourceMetadataImage;
    }

    public List<Boolean> getCreateNewVersionResourceImage() {
        return createNewVersionResourceImage;
    }

    public List<Boolean> getRollBackResourceImage() {
        return rollBackResourceImage;
    }

    public List<Boolean> getDeleteResourceImage() {
        return deleteResourceImage;
    }

    public List<Boolean> getEditResourceMetadataFont() {
        return editResourceMetadataFont;
    }

    public List<Boolean> getCreateNewVersionResourceFont() {
        return createNewVersionResourceFont;
    }

    public List<Boolean> getRollBackResourceFont() {
        return rollBackResourceFont;
    }

    public List<Boolean> getDeleteResourceFont() {
        return deleteResourceFont;
    }

    public List<Boolean> getEditResourceMetadataStylesheet() {
        return editResourceMetadataStylesheet;
    }

    public List<Boolean> getCreateNewVersionResourceStylesheet() {
        return createNewVersionResourceStylesheet;
    }

    public List<Boolean> getRollBackResourceStylesheet() {
        return rollBackResourceStylesheet;
    }

    public List<Boolean> getDeleteResourceStylesheet() {
        return deleteResourceStylesheet;
    }
}
