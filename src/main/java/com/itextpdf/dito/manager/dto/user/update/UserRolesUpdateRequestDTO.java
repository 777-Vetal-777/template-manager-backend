package com.itextpdf.dito.manager.dto.user.update;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserRolesUpdateRequestDTO {
    @NotEmpty
    private List<@NotBlank String> emails;
    @NotEmpty
    private List<@NotBlank String> roles;
    @NotNull
    private UpdateUsersRolesActionEnum updateUsersRolesActionEnum;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public UpdateUsersRolesActionEnum getUpdateUsersRolesActionEnum() {
        return updateUsersRolesActionEnum;
    }

    public void setUpdateUsersRolesActionEnum(
            UpdateUsersRolesActionEnum updateUsersRolesActionEnum) {
        this.updateUsersRolesActionEnum = updateUsersRolesActionEnum;
    }
}
