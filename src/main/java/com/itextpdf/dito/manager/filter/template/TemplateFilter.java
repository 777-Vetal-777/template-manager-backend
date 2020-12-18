package com.itextpdf.dito.manager.filter.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import java.util.List;

public class TemplateFilter {
    private String name;
    private String dataCollection;
    //always array with two dates as string from FE
    private List<String> editedOn;
    private String modifiedBy;
    private List<TemplateTypeEnum> type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(List<String> editedOn) {
        this.editedOn = editedOn;
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
}
