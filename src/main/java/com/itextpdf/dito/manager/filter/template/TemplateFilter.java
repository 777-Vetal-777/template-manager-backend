package com.itextpdf.dito.manager.filter.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import java.util.List;

public class TemplateFilter {
    private String name;
    private String dataCollection;
    //always array with two dates as string from FE
    private List<String> modifiedOn;
    private String modifiedBy;
    private List<TemplateTypeEnum> type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(List<String> modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

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
        return "TemplateFilter{" +
                "name='" + name + '\'' +
                ", dataCollection='" + dataCollection + '\'' +
                ", modifiedOn=" + modifiedOn +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", type=" + type +
                '}';
    }
}
