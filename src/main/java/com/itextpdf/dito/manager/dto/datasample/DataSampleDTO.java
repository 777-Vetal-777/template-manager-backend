package com.itextpdf.dito.manager.dto.datasample;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DataSampleDTO {
    private String name;
    private String modifiedBy;
    private Date modifiedOn;
    private Date createdOn;
    @JsonProperty("author.firstName")
    private String authorFirstName;
    @JsonProperty("author.lastName")
    private String authorLastName;
    private String fileName;
    private String comment;
    private String file;
    private Boolean setAsDefault;
    private Boolean isActual;
    
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
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


	public Boolean getSetAsDefault() {
		return setAsDefault;
	}

	public void setSetAsDefault(Boolean setAsDefault) {
		this.setAsDefault = setAsDefault;
	}

	public Boolean getIsActual() {
		return isActual;
	}

	public void setIsActual(Boolean isActual) {
		this.isActual = isActual;
	}

}