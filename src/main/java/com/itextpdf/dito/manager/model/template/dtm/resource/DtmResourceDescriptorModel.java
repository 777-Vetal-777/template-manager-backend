package com.itextpdf.dito.manager.model.template.dtm.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.model.template.dtm.AbstractDtmItemDescriptorModel;

public class DtmResourceDescriptorModel extends AbstractDtmItemDescriptorModel<DtmResourceVersionDescriptorModel> {
    private ResourceTypeEnum type;
    private String alias;

    public ResourceTypeEnum getType() {
        return type;
    }

    public void setType(ResourceTypeEnum type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
