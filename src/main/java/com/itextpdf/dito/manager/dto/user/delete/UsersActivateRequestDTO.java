package com.itextpdf.dito.manager.dto.user.delete;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class UsersActivateRequestDTO {
    @NotEmpty
    private List<String> emails;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
