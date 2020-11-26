package com.itextpdf.dito.manager.dto.user.unblock;

import java.util.List;

public class UserUnblockRequestDTO {
    private List<String> userEmails;

    public List<String> getUserEmails() {
        return userEmails;
    }

    public void setUserEmails(List<String> userEmails) {
        this.userEmails = userEmails;
    }
}
