package com.itextpdf.dito.manager.model.template.dtm;

import java.util.List;

public abstract class AbstractDtmItemDescriptorModel<V> {
    private List<V> versions;
    private String id;
    private String name;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<V> getVersions() {
        return versions;
    }

    public void setVersions(List<V> versions) {
        this.versions = versions;
    }

}
