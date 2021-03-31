package com.itextpdf.dito.manager.model.template;

import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import java.util.Date;
import java.util.Set;

public class TemplateModelWithRoles {
    private String name;
    private TemplateTypeEnum type;
    private String dataCollection;
    private String author;
    private Date lastUpdate;
    private Long version;
    private Date createdOn;
    private String createdBy;
    private String comment;
    private Set<RoleDTO> appliedRoles;

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

    public String getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(String dataCollection) {
        this.dataCollection = dataCollection;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<RoleDTO> getAppliedRoles() {
        return appliedRoles;
    }

    public void setAppliedRoles(Set<RoleDTO> appliedRoles) {
        this.appliedRoles = appliedRoles;
    }
}
