package com.itextpdf.dito.manager.dto.role;

import com.itextpdf.dito.manager.dto.permission.PermissionDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RoleDTO {
    private Long id;
    private String name;
    private String type;
    private Boolean master;
    @JsonProperty("users")
    private List<String> usersEmails;
    private List<PermissionDTO> permissions;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
    }

    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }

    public List<String> getUsersEmails() {
        return usersEmails;
    }

    public void setUsersEmails(List<String> usersEmails) {
        this.usersEmails = usersEmails;
    }
}
