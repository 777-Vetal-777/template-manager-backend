package com.itextpdf.dito.manager.component.mail.impl;

import com.itextpdf.dito.manager.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailClientImplTest {

    private final MailClientImpl mailClient = new MailClientImpl("localhost", 505, "test", "12345", false, false, "localhost:8080", "localhost:8080", "mailsender@example.com");

    private UserEntity userEntity;

    @BeforeEach
    public void initDb(){
        userEntity = new UserEntity();
        userEntity.setEmail("test");
        userEntity.setFirstName("FirstName");
        userEntity.setLastName("LastName");
    }
    @Test
    void sendRegistrationMessage() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        ReflectionTestUtils.setField(mailClient, "client", mailSender);
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        Assertions.assertDoesNotThrow(()-> mailClient.sendRegistrationMessage(userEntity, "12345", userEntity));
    }

    @Test
    void sendAdminUpdatedPasswordsMessage() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        ReflectionTestUtils.setField(mailClient, "client", mailSender);
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        Assertions.assertDoesNotThrow(()-> mailClient.sendPasswordsWasUpdatedByAdminMessage(userEntity, "12345", userEntity));
    }

    @Test
    void sendResetTokenMessage() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        ReflectionTestUtils.setField(mailClient, "client", mailSender);
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        Assertions.assertDoesNotThrow(()-> mailClient.sendResetMessage(userEntity, "12345"));
    }
}