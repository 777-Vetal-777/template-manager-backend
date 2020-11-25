package com.itextpdf.dito.manager.component.mail;

public interface MailClient {
    void send(String from, String to, String subject, String text);
}
