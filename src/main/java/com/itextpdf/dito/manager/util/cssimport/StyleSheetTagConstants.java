package com.itextpdf.dito.manager.util.cssimport;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;

public final class StyleSheetTagConstants {

    public static final String STYLESHEET_NAME = "data-dito-stylesheet-name";
    public static final String STYLESHEET_INTERNAL_NAME = "data-dito-internal-style";
    public static final String DITO_ASSET_HREF = DITO_ASSET_TAG;

    private StyleSheetTagConstants() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
