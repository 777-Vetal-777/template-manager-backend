package com.itextpdf.dito.manager.dto.datacollection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itextpdf.dito.manager.dto.role.RoleDTO;

import java.util.Date;
import java.util.Set;

public class DataCollectionDTO {
    private String name;
    private String description;
    private DataCollectionType type;
    private String modifiedBy;
    private Date modifiedOn;
    private Date createdOn;
    @JsonProperty("author.firstName")
    private String authorFirstName;
    @JsonProperty("author.lastName")
    private String authorLastName;
    private String fileName;
    private String comment;
    private Long version;
    private String attachment;

    private Set<RoleDTO> appliedRoles;

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

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
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

    public Set<RoleDTO> getAppliedRoles() {
        return appliedRoles;
    }

    public void setAppliedRoles(Set<RoleDTO> appliedRoles) {
        this.appliedRoles = appliedRoles;
    }
}