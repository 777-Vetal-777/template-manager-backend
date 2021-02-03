package com.itextpdf.dito.manager.component.mail;

import com.itextpdf.dito.manager.entity.UserEntity;

public interface MailClient {
    void sendRegistrationMessage(UserEntity savedUser, String password, UserEntity currentUser);

    void sendResetMessage(UserEntity userEntity, String token);
}
