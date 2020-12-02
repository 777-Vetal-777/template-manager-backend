package com.itextpdf.dito.manager.dto.template;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class TemplateDTO {
    private String name;
    @JsonProperty("type.name")
    private String type;
    @JsonProperty("dataCollection")
    private String dataCollection;
    @JsonProperty("file.author")
    private String author;
    @JsonProperty("file.version")
    private Date lastUpdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
