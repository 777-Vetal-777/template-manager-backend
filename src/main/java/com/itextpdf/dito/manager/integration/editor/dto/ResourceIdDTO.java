package com.itextpdf.dito.manager.integration.editor.dto;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;

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

    @Override
    public String toString() {
        return "ResourceIdDTO{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", subName='" + subName + '\'' +
                '}';
    }
}
