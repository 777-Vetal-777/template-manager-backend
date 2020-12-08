package com.itextpdf.dito.manager.dto.datacollection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DataCollectionDTO {
    private String name;
    private String description;
    private DataCollectionType type;
    @JsonProperty("author.firstName")
    private String authorFirstName;
    @JsonProperty("author.lastName")
    private String authorLastName;
    private Date modifiedOn;
    private Date createdOn;
    private String fileName;
    private String attachment;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
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

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}