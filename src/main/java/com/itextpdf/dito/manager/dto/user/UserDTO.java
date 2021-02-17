package com.itextpdf.dito.manager.dto.user;

import com.itextpdf.dito.manager.dto.role.RoleDTO;

import java.util.List;

public class UserDTO {
    private String email;
    private String firstName;
    private String lastName;
    private Boolean active;
    private Boolean blocked;
    private List<RoleDTO> roles;
    private List<String> authorities;
    private Boolean passwordUpdatedByAdmin;

    public Boolean getPasswordUpdatedByAdmin() {
        return passwordUpdatedByAdmin;
    }

    public void setPasswordUpdatedByAdmin(Boolean passwordUpdatedByAdmin) {
        this.passwordUpdatedByAdmin = passwordUpdatedByAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
