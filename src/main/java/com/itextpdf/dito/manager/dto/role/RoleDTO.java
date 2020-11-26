package com.itextpdf.dito.manager.dto.role;

import com.itextpdf.dito.manager.dto.user.UserDTO;

import java.util.List;

public class RoleDTO {
    private Long id;
    private String name;
    private RoleType type;
    private List<UserDTO> users;

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

    public RoleType getType() {
        return type;
    }

    public void setType(RoleType type) {
        this.type = type;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}
