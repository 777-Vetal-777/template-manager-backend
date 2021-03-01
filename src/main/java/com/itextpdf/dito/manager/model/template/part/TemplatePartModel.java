package com.itextpdf.dito.manager.model.template.part;

public class TemplatePartModel {
    private String templateName;
    private String condition;
    private PartSettings partSettings;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public PartSettings getPartSettings() {
        return partSettings;
    }

    public void setPartSettings(PartSettings partSettings) {
        this.partSettings = partSettings;
    }

    @Override
    public String toString() {
        return "TemplatePartModel{" +
                "templateName='" + templateName + '\'' +
                ", condition='" + condition + '\'' +
                ", partSettings=" + partSettings +
                '}';
    }
}
