package com.itextpdf.dito.manager.dto.resource;

public enum ResourceTypeEnum {
    IMAGE("images");

    public final String pluralName;

    ResourceTypeEnum(String name) {
        this.pluralName = name;
    }

    public static ResourceTypeEnum getEnum(String name) {
        for (ResourceTypeEnum resourceType : values()) {
            if (resourceType.pluralName.equals(name)) {
                return resourceType;
            }
        }
        return null;
    }
}
