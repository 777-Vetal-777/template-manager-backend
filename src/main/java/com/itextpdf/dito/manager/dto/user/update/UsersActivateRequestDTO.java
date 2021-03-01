package com.itextpdf.dito.manager.dto.user.update;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UsersActivateRequestDTO {
    @NotNull
    private boolean activate;
    @NotEmpty
    private List<String> emails;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    @Override
    public String toString() {
        return "UsersActivateRequestDTO{" +
                "activate=" + activate +
                ", emails=" + emails +
                '}';
    }
}
