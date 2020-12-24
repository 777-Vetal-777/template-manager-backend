package com.itextpdf.dito.manager.entity;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "permission")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "permission_gen")
    @SequenceGenerator(name = "permission_gen", sequenceName = "permission_sequence", allocationSize = 1)
    private Long id;
    private String name;
    @Column(name = "optional_for_custom_role")
    private Boolean optionalForCustomRole;

    @ManyToMany(mappedBy = "permissions")
    private Set<RoleEntity> roles;

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

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public Boolean getOptionalForCustomRole() {
        return optionalForCustomRole == null ? Boolean.FALSE : optionalForCustomRole;
    }

    public void setOptionalForCustomRole(Boolean optionalForCustomRole) {
        this.optionalForCustomRole = optionalForCustomRole;
    }
}
