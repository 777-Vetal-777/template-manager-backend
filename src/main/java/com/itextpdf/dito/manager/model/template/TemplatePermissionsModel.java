package com.itextpdf.dito.manager.model.template;

public interface TemplatePermissionsModel {
    String getName();
    String getType();
    String getTemplateType();
    Boolean getE9_US75_EDIT_TEMPLATE_METADATA_STANDARD();
    Boolean getE9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD();
    Boolean getE9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE();
    Boolean getE9_US81_PREVIEW_TEMPLATE_STANDARD();
    Boolean getE9_US24_EXPORT_TEMPLATE_DATA();
    Boolean getE9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE();
    Boolean getE9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED();
}
