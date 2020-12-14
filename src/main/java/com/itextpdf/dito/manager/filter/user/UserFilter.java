package com.itextpdf.dito.manager.filter.user;

import java.util.List;

public class UserFilter {
    private String email;
    private String firstName;
    private String lastName;
    private List<String> securityRoles;
    private Boolean active;

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

    public List<String> getSecurityRoles() {
        return securityRoles;
    }

    public void setSecurityRoles(List<String> securityRoles) {
        this.securityRoles = securityRoles;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
