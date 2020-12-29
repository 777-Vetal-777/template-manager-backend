package com.itextpdf.dito.manager.dto.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class TemplateDTO {
    private String name;
    @JsonProperty("type")
    private TemplateTypeEnum type;
    @JsonProperty("dataCollection")
    private String dataCollection;
    @JsonProperty("modifiedBy")
    private String author;
    @JsonProperty("modifiedOn")
    private Date lastUpdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TemplateTypeEnum getType() {
        return type;
    }

    public void setType(TemplateTypeEnum type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(String dataCollection) {
        this.dataCollection = dataCollection;
    }
}
