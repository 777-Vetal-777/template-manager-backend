package com.itextpdf.dito.manager.dto.datacollection;

public class DataCollectionCreateRequestDTO {
    private String name;
    private DataCollectionType type;

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
