package com.itextpdf.dito.manager.entity.resource;

public enum FontTypeEnum {
    REGULAR("regular"),
    BOLD("bold"),
    ITALIC("italic"),
    BOLD_ITALIC("bold_italic");
    public final String fontFace;

    FontTypeEnum(final String name) {
        this.fontFace = name;
    }
}
