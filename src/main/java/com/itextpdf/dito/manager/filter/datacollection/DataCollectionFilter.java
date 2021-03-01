package com.itextpdf.dito.manager.filter.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;

import java.util.List;

public class DataCollectionFilter {
    private String name;
    private List<String> modifiedOn;
    private List<DataCollectionType> type;
    private String modifiedBy;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<String> getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(List<String> modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public List<DataCollectionType> getType() {
        return type;
    }

    public void setType(List<DataCollectionType> type) {
        this.type = type;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "DataCollectionFilter{" +
                "name='" + name + '\'' +
                ", modifiedOn=" + modifiedOn +
                ", type=" + type +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }
}
