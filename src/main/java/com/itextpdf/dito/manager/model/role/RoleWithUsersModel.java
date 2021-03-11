package com.itextpdf.dito.manager.model.role;

import com.itextpdf.dito.manager.dto.permission.PermissionDTO;

import java.util.ArrayList;
import java.util.List;

public class RoleWithUsersModel {
    private Long id;
    private String name;
    private String type;
    private Boolean master;
    private List<String> usersEmails;
    private List<PermissionDTO> permissions = new ArrayList<>();

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

    public List<String> getUsersEmails() {
        return usersEmails;
    }

    public void setUsersEmails(List<String> usersEmails) {
        this.usersEmails = usersEmails;
    }

    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
}
