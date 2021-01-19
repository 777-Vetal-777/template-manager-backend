package com.itextpdf.dito.manager.integration.editor.dto.template;

public class TemplateDescriptor {
    private String id;
    private String displayName;
    private String dataCollectionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDataCollectionId() {
        return dataCollectionId;
    }

    public void setDataCollectionId(String dataCollectionId) {
        this.dataCollectionId = dataCollectionId;
    }
}
