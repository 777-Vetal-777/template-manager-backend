package com.itextpdf.dito.manager.model.template.dtm.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.model.template.dtm.AbstractDtmItemDescriptorModel;

public class DtmTemplateDescriptorModel extends AbstractDtmItemDescriptorModel<DtmTemplateVersionDescriptorModel> {
    private TemplateTypeEnum type;
    private String alias;

    public TemplateTypeEnum getType() {
        return type;
    }

    public void setType(TemplateTypeEnum type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "DtmTemplateDescriptorModel{" +
                ", type=" + type +
                ", alias='" + alias + '\'' +
                "} " + super.toString();
    }
}
