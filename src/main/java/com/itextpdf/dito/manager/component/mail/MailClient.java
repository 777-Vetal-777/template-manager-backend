package com.itextpdf.dito.manager.component.mail;

public interface MailClient {
    void sendRegistrationMessage(String email, String password);
}
