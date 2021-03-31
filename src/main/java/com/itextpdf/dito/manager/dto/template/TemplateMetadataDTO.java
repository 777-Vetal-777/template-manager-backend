package com.itextpdf.dito.manager.dto.template;

import com.itextpdf.dito.manager.dto.template.version.TemplateDeployedVersionDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class TemplateMetadataDTO{

    private String name;
    @JsonProperty("type")
    private TemplateTypeEnum type;
    private String dataCollection;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;
    private String description;
    private Long version;
    private Boolean blocked;
    private String blockedBy;
    private List<TemplateDeployedVersionDTO> deployedVersions;
    private Set<String> permissions;

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public TemplateTypeEnum getType() {
        return type;
    }

    public void setType(TemplateTypeEnum type) {
        this.type = type;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(String dataCollection) {
        this.dataCollection = dataCollection;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public List<TemplateDeployedVersionDTO> getDeployedVersions() {
        return deployedVersions;
    }

    public void setDeployedVersions(List<TemplateDeployedVersionDTO> deployedVersions) {
        this.deployedVersions = deployedVersions;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }
}
