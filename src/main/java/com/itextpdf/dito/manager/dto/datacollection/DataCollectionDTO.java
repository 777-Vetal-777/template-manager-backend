package com.itextpdf.dito.manager.dto.datacollection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DataCollectionDTO {
    private Long id;
    private String name;
    private DataCollectionType type;
    @JsonProperty("author.email")
    private String author;
    private Date modifiedOn;

    public DataCollectionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
