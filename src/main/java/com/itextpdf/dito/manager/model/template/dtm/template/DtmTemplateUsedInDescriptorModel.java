package com.itextpdf.dito.manager.model.template.dtm.template;

import com.itextpdf.dito.manager.model.template.dtm.DtmUsedInDescriptorModel;

public class DtmTemplateUsedInDescriptorModel extends DtmUsedInDescriptorModel {
    private String conditions;
    private String settings;

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "DtmTemplateUsedInDescriptorModel{" +
                "conditions='" + conditions + '\'' +
                ", settings='" + settings + '\'' +
                "} " + super.toString();
    }
}
