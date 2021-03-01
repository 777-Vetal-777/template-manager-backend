package com.itextpdf.dito.manager.dto.user.unblock;

import java.util.List;
import javax.validation.constraints.NotEmpty;

public class UsersUnblockRequestDTO {
    @NotEmpty
    private List<String> userEmails;

    public List<String> getUserEmails() {
        return userEmails;
    }

    public void setUserEmails(List<String> userEmails) {
        this.userEmails = userEmails;
    }

    @Override
    public String toString() {
        return "UsersUnblockRequestDTO{" +
                "userEmails=" + userEmails +
                '}';
    }
}
