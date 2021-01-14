package com.itextpdf.dito.manager.dto.template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplatePermissionDTO {

    private String name;
    private String type;
    @JsonProperty("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD")
    private Boolean editMetadataPermission;
    @JsonProperty("E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD")
    private Boolean createNewVersionPermission;
    @JsonProperty("E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE")
    private Boolean rollbackPermission;
    @JsonProperty("E9_US81_PREVIEW_TEMPLATE_STANDARD")
    private Boolean previewTemplatePermission;
    @JsonProperty("E9_US24_EXPORT_TEMPLATE_DATA")
    private Boolean exportTemplatePermission;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEditMetadataPermission() {
        return editMetadataPermission;
    }

    public void setEditMetadataPermission(Boolean editMetadataPermission) {
        this.editMetadataPermission = editMetadataPermission;
    }

    public Boolean getCreateNewVersionPermission() {
        return createNewVersionPermission;
    }

    public void setCreateNewVersionPermission(Boolean createNewVersionPermission) {
        this.createNewVersionPermission = createNewVersionPermission;
    }

    public Boolean getRollbackPermission() {
        return rollbackPermission;
    }

    public void setRollbackPermission(Boolean rollbackPermission) {
        this.rollbackPermission = rollbackPermission;
    }

    public Boolean getPreviewTemplatePermission() {
        return previewTemplatePermission;
    }

    public void setPreviewTemplatePermission(Boolean previewTemplatePermission) {
        this.previewTemplatePermission = previewTemplatePermission;
    }

    public Boolean getExportTemplatePermission() {
        return exportTemplatePermission;
    }

    public void setExportTemplatePermission(Boolean exportTemplatePermission) {
        this.exportTemplatePermission = exportTemplatePermission;
    }
}
