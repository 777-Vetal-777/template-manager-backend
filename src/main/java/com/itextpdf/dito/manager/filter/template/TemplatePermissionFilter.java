package com.itextpdf.dito.manager.filter.template;


import java.util.List;

public class TemplatePermissionFilter {
    private List<String> name;
    private List<Boolean> editTemplateMetadata;
    private List<Boolean> createNewTemplateVersionStandard;
    private List<Boolean> rollbackVersionStandard;
    private List<Boolean> previewTemplate;
    private List<Boolean> exportTemplate;
    private List<Boolean> createNewTemplateVersionComposition;
    private List<Boolean> rollbackVersionComposition;


    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    /* following setter methods added to reach request params mapping to fields in REST Controller */

    public void setE9_US75_EDIT_TEMPLATE_METADATA_STANDARD(List<Boolean> editTemplateMetadata) {
        this.editTemplateMetadata = editTemplateMetadata;
    }

    public void setE9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD(List<Boolean> createNewTemplateVersionStandard) {
        this.createNewTemplateVersionStandard = createNewTemplateVersionStandard;
    }

    public void setE9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE(List<Boolean> rollbackVersionStandard) {
        this.rollbackVersionStandard = rollbackVersionStandard;
    }

    public void setE9_US81_PREVIEW_TEMPLATE_STANDARD(List<Boolean> previewTemplate) {
        this.previewTemplate = previewTemplate;
    }

    public void setE9_US24_EXPORT_TEMPLATE_DATA(List<Boolean> exportTemplate) {
        this.exportTemplate = exportTemplate;
    }

    public void setE9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED(List<Boolean> createNewTemplateVersionComposition) {
        this.createNewTemplateVersionComposition = createNewTemplateVersionComposition;
    }

    public void setE9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE(List<Boolean> rollbackVersionComposition) {
        this.rollbackVersionComposition = rollbackVersionComposition;
    }

    public List<Boolean> getEditTemplateMetadata() {
        return editTemplateMetadata;
    }

    public List<Boolean> getCreateNewTemplateVersionStandard() {
        return createNewTemplateVersionStandard;
    }

    public List<Boolean> getRollbackVersionStandard() {
        return rollbackVersionStandard;
    }

    public List<Boolean> getPreviewTemplate() {
        return previewTemplate;
    }

    public List<Boolean> getExportTemplate() {
        return exportTemplate;
    }

    public List<Boolean> getCreateNewTemplateVersionComposition() {
        return createNewTemplateVersionComposition;
    }

    public List<Boolean> getRollbackVersionComposition() {
        return rollbackVersionComposition;
    }

    @Override
    public String toString() {
        return "TemplatePermissionFilter{" +
                "name=" + name +
                ", editTemplateMetadata=" + editTemplateMetadata +
                ", createNewTemplateVersionStandard=" + createNewTemplateVersionStandard +
                ", rollbackVersionStandard=" + rollbackVersionStandard +
                ", previewTemplate=" + previewTemplate +
                ", exportTemplate=" + exportTemplate +
                ", createNewTemplateVersionComposition=" + createNewTemplateVersionComposition +
                ", rollbackVersionComposition=" + rollbackVersionComposition +
                '}';
    }
}
