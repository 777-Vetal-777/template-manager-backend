package com.itextpdf.dito.manager.dto.resource;

public enum ResourceTypeEnum {
    IMAGE("images"),
    FONT("fonts"),
    STYLESHEET("stylesheets");

    public final String pluralName;

    ResourceTypeEnum(final String pluralName) {
        this.pluralName = pluralName;
    }

    public static ResourceTypeEnum getFromPluralName(final String pluralName) {
        ResourceTypeEnum result = null;

        for (final ResourceTypeEnum resourceType : values()) {
            if (resourceType.pluralName.equals(pluralName)) {
                result = resourceType;
            }
        }

        return result;
    }
}
