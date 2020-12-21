package com.itextpdf.dito.manager.component.mail.impl;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailClientImplTest {

    private final MailClientImpl mailClient = new MailClientImpl("localhost", 505, "test", "12345", false, false, "localhost:8080");

    @Test
    void sendRegistrationMessage() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        ReflectionTestUtils.setField(mailClient, "client", mailSender);
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        mailClient.sendRegistrationMessage("test", "12345");
    }
}