package com.itextpdf.dito.manager.model.template.dtm.template;

import com.itextpdf.dito.manager.model.template.dtm.AbstractDtmItemVersionDescriptorModel;

import java.util.List;

public class DtmTemplateVersionDescriptorModel extends AbstractDtmItemVersionDescriptorModel {
    private String alias;
    private List<DtmTemplateUsedInDescriptorModel> usedIn;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<DtmTemplateUsedInDescriptorModel> getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(List<DtmTemplateUsedInDescriptorModel> usedIn) {
        this.usedIn = usedIn;
    }

}
