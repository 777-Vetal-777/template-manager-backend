package com.itextpdf.dito.manager.entity;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "role_gen")
    @SequenceGenerator(name = "role_gen", sequenceName = "role_sequence", allocationSize = 1)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(
                    name = "role_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id"))
    private Set<PermissionEntity> permissions = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private RoleTypeEnum type;
    private Boolean master;

    @ManyToMany(mappedBy = "appliedRoles")
    private Set<ResourceEntity> resources = new HashSet<>();

    @ManyToMany(mappedBy = "appliedRoles")
    private Set<DataCollectionEntity> dataCollections = new HashSet<>();

    @ManyToMany(mappedBy = "appliedRoles")
    private Set<TemplateEntity> templates = new HashSet<>();

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

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    public Set<PermissionEntity> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionEntity> permissions) {
        this.permissions = permissions;
    }

    public RoleTypeEnum getType() {
        return type;
    }

    public void setType(RoleTypeEnum type) {
        this.type = type;
    }

    public Set<ResourceEntity> getResources() {
        return resources;
    }

    public void setResources(Set<ResourceEntity> resources) {
        this.resources = resources;
    }

    public Boolean getMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
    }

    public Set<DataCollectionEntity> getDataCollections() {
        return dataCollections;
    }

    public void setDataCollections(Set<DataCollectionEntity> dataCollections) {
        this.dataCollections = dataCollections;
    }

    public Set<TemplateEntity> getTemplates() {
        return templates;
    }

    public void setTemplates(Set<TemplateEntity> templates) {
        this.templates = templates;
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", master=" + master +
                '}';
    }
}
