package com.itextpdf.dito.manager.dto.user.update;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class PasswordChangeRequestDTO {
    @NotEmpty
    @Size(min = 12, max = 64)
    private String oldPassword;
    @NotEmpty
    @Size(min = 12, max = 64)
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
