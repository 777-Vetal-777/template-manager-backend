package com.itextpdf.dito.manager.entity.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "resource")
public class ResourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "resource_gen")
    @SequenceGenerator(name = "resource_gen", sequenceName = "resource_sequence", allocationSize = 1)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private ResourceTypeEnum type;
    @Column(insertable = false)
    private Date createdOn;
    @ManyToOne()
    @JoinColumn(name = "author_id")
    private UserEntity createdBy;
    @OneToMany(
            mappedBy = "resource",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("version DESC")
    private Collection<ResourceFileEntity> resourceFiles;
    @OneToMany(
            mappedBy = "resource",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("date DESC")
    private Collection<ResourceLogEntity> resourceLogs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Collection<ResourceFileEntity> getResourceFiles() {
        return resourceFiles;
    }

    public void setResourceFiles(Collection<ResourceFileEntity> resourceFiles) {
        this.resourceFiles = resourceFiles;
    }

    public Collection<ResourceLogEntity> getResourceLogs() {
        return resourceLogs;
    }

    public void setResourceLogs(Collection<ResourceLogEntity> resourceLog) {
        this.resourceLogs = resourceLog;
    }

    public void setName(String name) {
        this.name = name;
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

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


}