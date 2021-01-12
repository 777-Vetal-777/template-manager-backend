package com.itextpdf.dito.manager.filter.template;

import java.util.List;

public class TemplatePermissionFilter {
    private String name;
    private List<Boolean> editTemplateMetadata;
    private List<Boolean> createNewTemplateVersion;
    private List<Boolean> rollbackVersion;
    private List<Boolean> previewTemplate;
    private List<Boolean> exportTemplate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Boolean> getEditTemplateMetadata() {
        return editTemplateMetadata;
    }

    public void setEditTemplateMetadata(List<Boolean> editTemplateMetadata) {
        this.editTemplateMetadata = editTemplateMetadata;
    }

    public List<Boolean> getCreateNewTemplateVersion() {
        return createNewTemplateVersion;
    }

    public void setCreateNewTemplateVersion(List<Boolean> createNewTemplateVersion) {
        this.createNewTemplateVersion = createNewTemplateVersion;
    }

    public List<Boolean> getRollbackVersion() {
        return rollbackVersion;
    }

    public void setRollbackVersion(List<Boolean> rollbackVersion) {
        this.rollbackVersion = rollbackVersion;
    }

    public List<Boolean> getPreviewTemplate() {
        return previewTemplate;
    }

    public void setPreviewTemplate(List<Boolean> previewTemplate) {
        this.previewTemplate = previewTemplate;
    }

    public List<Boolean> getExportTemplate() {
        return exportTemplate;
    }

    public void setExportTemplate(List<Boolean> exportTemplate) {
        this.exportTemplate = exportTemplate;
    }

    /* following setter methods added to reach request params mapping to fields in REST Controller */

    public void setE9_US75_EDIT_TEMPLATE_METADATA_STANDARD(List<Boolean> E9_US75_EDIT_TEMPLATE_METADATA_STANDARD) {
        setEditTemplateMetadata(E9_US75_EDIT_TEMPLATE_METADATA_STANDARD);
    }

    public void setE9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD(List<Boolean> E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD) {
        setCreateNewTemplateVersion(E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD);
    }

    public void setE9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE(List<Boolean> E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE) {
        setRollbackVersion(E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE);
    }

    public void setE9_US81_PREVIEW_TEMPLATE_STANDARD(List<Boolean> E9_US81_PREVIEW_TEMPLATE_STANDARD) {
        setPreviewTemplate(E9_US81_PREVIEW_TEMPLATE_STANDARD);
    }

    public void setE9_US24_EXPORT_TEMPLATE_DATA(List<Boolean> E9_US24_EXPORT_TEMPLATE_DATA) {
        setExportTemplate(E9_US24_EXPORT_TEMPLATE_DATA);
    }

}
