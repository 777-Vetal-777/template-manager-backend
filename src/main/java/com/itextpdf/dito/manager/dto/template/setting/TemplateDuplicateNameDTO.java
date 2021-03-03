package com.itextpdf.dito.manager.dto.template.setting;

public class TemplateDuplicateNameDTO {
    private String name;
    private SettingType type;

    public SettingType getType() {
        return type;
    }

    public void setType(SettingType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
