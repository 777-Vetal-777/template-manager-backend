package com.itextpdf.dito.manager.dto.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ResourceDTO {
    private String name;
    private String comment;
    private String description;
    private ResourceTypeEnum type;
    @JsonProperty("author.firstName")
    private String authorFirstName;
    @JsonProperty("author.lastName")
    private String authorLastName;
    private Date createdOn;
    @JsonProperty("modified.firstName")
    private String modifiedFirstName;
    @JsonProperty("modified.lastName")
    private String modifiedLastName;
    private Date modifiedOn;
    private Long version;
    private String coment;
    private String fileName;
    private byte[] file;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getComent() {
        return coment;
    }

    public void setComent(String coment) {
        this.coment = coment;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ResourceTypeEnum getType() {
        return type;
    }

    public void setType(ResourceTypeEnum type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getModifiedFirstName() {
        return modifiedFirstName;
    }

    public void setModifiedFirstName(String modifiedFirstName) {
        this.modifiedFirstName = modifiedFirstName;
    }

    public String getModifiedLastName() {
        return modifiedLastName;
    }

    public void setModifiedLastName(String modifiedLastName) {
        this.modifiedLastName = modifiedLastName;
    }
}
