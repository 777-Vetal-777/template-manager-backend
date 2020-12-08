package com.itextpdf.dito.manager.dto.datacollection.update;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;

public class DataCollectionUpdateRequestDTO {
    private String name;
    private DataCollectionType type;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataCollectionType getType() {
        return type;
    }

    public void setType(DataCollectionType type) {
        this.type = type;
    }
}
