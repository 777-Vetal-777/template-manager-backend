package com.itextpdf.dito.manager.dto.resource;
import java.util.List;

public class ResourceIdDTO {
    private String name;
    private ResourceTypeEnum type;
    private List<String> subResources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceTypeEnum getType() {
        return type;
    }

    public void setType(ResourceTypeEnum type) {
        this.type = type;
    }

    public List<String> getSubResources() {
        return subResources;
    }

    public void setSubResources(List<String> subResources) {
        this.subResources = subResources;
    }
}
