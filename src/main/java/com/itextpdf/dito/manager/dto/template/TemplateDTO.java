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
    private Long version;
    private Date createdOn;
    private String createdBy;
    private String comment;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

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
