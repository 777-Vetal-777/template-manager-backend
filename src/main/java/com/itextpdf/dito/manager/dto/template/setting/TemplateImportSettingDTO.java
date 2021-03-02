package com.itextpdf.dito.manager.dto.template.setting;

public class TemplateImportSettingDTO extends TemplateImportNameModel {
    private SettingType type;

    public SettingType getType() {
        return type;
    }

    public void setType(SettingType type) {
        this.type = type;
    }
}
