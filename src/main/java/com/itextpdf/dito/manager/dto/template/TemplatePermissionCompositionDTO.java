package com.itextpdf.dito.manager.dto.template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplatePermissionCompositionDTO extends TemplatePermissionDTO {

    @Override
    @JsonProperty("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD")
    public void setEditMetadataPermission(Boolean editMetadataPermission) {
        this.editMetadataPermission = editMetadataPermission;
    }

    @Override
    @JsonProperty("E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE")
    public void setRollbackPermission(Boolean rollbackPermission) {
        this.rollbackPermission = rollbackPermission;
    }

    @Override
    @JsonProperty("E9_US81_PREVIEW_TEMPLATE_STANDARD")
    public void setPreviewTemplatePermission(Boolean previewTemplatePermission) {
        this.previewTemplatePermission = previewTemplatePermission;
    }

    @Override
    @JsonProperty("E9_US24_EXPORT_TEMPLATE_DATA")
    public void setExportTemplatePermission(Boolean exportTemplatePermission) {
        this.exportTemplatePermission = exportTemplatePermission;
    }

    @Override
    @JsonProperty("E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED")
    public void setCreateNewVersionPermission(Boolean createNewVersionPermission) {
        this.createNewVersionPermission = createNewVersionPermission;
    }
}
