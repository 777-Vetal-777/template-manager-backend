package com.itextpdf.dito.manager.model.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.role.RoleDTO;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ResourceModelWithRoles {
    private Long id;

    private Long resourceId;
    private String name;

    private String comment;

    private String description;

    private ResourceTypeEnum type;

    private String authorFirstName;

    private String authorLastName;

    private Date createdOn;

    private String modifiedBy;

    private Date modifiedOn;

    private Long version;

    private Boolean deployed;

    private Set<RoleDTO> appliedRoles;

    private List<MetaInfoModel> metadataUrls;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public List<MetaInfoModel> getMetadataUrls() {
        return metadataUrls;
    }

    public void setMetadataUrls(List<MetaInfoModel> metadataUrls) {
        this.metadataUrls = metadataUrls;
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

    public ResourceTypeEnum getType() {
        return type;
    }

    public void setType(ResourceTypeEnum type) {
        this.type = type;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeployed() {
        return deployed;
    }

    public void setDeployed(Boolean deployed) {
        this.deployed = deployed;
    }

    public Set<RoleDTO> getAppliedRoles() {
        return appliedRoles;
    }

    public void setAppliedRoles(Set<RoleDTO> appliedRoles) {
        this.appliedRoles = appliedRoles;
    }
}
