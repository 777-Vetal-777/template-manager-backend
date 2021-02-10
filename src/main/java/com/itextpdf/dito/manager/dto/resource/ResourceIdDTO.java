package com.itextpdf.dito.manager.dto.resource;

public class ResourceIdDTO {
    private String name;
    private ResourceTypeEnum type;
    private String subName;
    
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

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}
    
}
