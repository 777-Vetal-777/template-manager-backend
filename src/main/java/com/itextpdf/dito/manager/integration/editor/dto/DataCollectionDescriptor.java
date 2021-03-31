package com.itextpdf.dito.manager.integration.editor.dto;

public class DataCollectionDescriptor {

    private String displayName;
    private String id;
    private String type;
    private String defaultSampleId;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultSampleId() {
        return defaultSampleId;
    }

    public void setDefaultSampleId(String defaultSampleId) {
        this.defaultSampleId = defaultSampleId;
    }
}
