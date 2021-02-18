package com.itextpdf.dito.manager.dto.template;


public class TemplatePermissionDTO {

    private String name;
    private String type;
    protected Boolean editMetadataPermission;
    protected Boolean createNewVersionPermission;
    protected Boolean rollbackPermission;
    protected Boolean previewTemplatePermission;
    protected Boolean exportTemplatePermission;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
