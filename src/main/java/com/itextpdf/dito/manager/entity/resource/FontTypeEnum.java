package com.itextpdf.dito.manager.entity.resource;

public enum FontTypeEnum {
    REGULAR("regular"),
    BOLD("bold"),
    ITALIC("italic"),
    BOLD_ITALIC("bold_italic");
    public final String name;

    FontTypeEnum(final String name) {
        this.name = name;
    }
}
