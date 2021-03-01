package com.itextpdf.dito.manager.filter.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import java.util.List;

public class TemplateListFilter {
    private String dataCollection;
    private List<TemplateTypeEnum> type;

    public String getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(String dataCollection) {
        this.dataCollection = dataCollection;
    }

    public List<TemplateTypeEnum> getType() {
        return type;
    }

    public void setType(List<TemplateTypeEnum> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TemplateListFilter{" +
                "dataCollection='" + dataCollection + '\'' +
                ", type=" + type +
                '}';
    }
}
