package com.itextpdf.dito.manager.component.mail;

import com.itextpdf.dito.manager.entity.UserEntity;

public interface MailClient {
    void sendRegistrationMessage(UserEntity savedUser, String password, UserEntity currentUser, String frontURL);

    void sendPasswordsWasUpdatedByAdminMessage(UserEntity savedUser, String password, UserEntity admin, String frontURL);

    void sendResetMessage(UserEntity userEntity, String token, String frontURL);
}
