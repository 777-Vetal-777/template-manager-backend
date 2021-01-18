package com.itextpdf.dito.manager.filter.resource;


import java.util.List;


public class ResourcePermissionFilter {
    private List<String> name;
    private List<Boolean> editResourceMetadataImage;
    private List<Boolean> createNewVersionResourceImage;
    private List<Boolean> rollBackResourceImage;
    private List<Boolean> deleteResourceImage;

    public ResourcePermissionFilter(List<String> name, List<Boolean> editResourceMetadataImage,
                                    List<Boolean> createNewVersionResourceImage, List<Boolean> rollBackResourceImage,
                                    List<Boolean> deleteResourceImage) {
        this.name = name;
        this.editResourceMetadataImage = editResourceMetadataImage;
        this.createNewVersionResourceImage = createNewVersionResourceImage;
        this.rollBackResourceImage = rollBackResourceImage;
        this.deleteResourceImage = deleteResourceImage;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
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
}
