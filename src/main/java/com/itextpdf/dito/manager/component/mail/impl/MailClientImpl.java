package com.itextpdf.dito.manager.component.mail.impl;

import com.itextpdf.dito.manager.component.mail.MailClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@ConditionalOnProperty(value = "ditomanager.mailing.enabled", havingValue = "true")
public class MailClientImpl implements MailClient {
    private static final Logger log = LogManager.getLogger(MailClientImpl.class);

    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    private final Boolean auth;
    private final Boolean tls;

    private JavaMailSender client;

    private final String MAIL_FROM = "ditotemplatemanager@gmail.com";
    private final String MAIL_BODY = "<p>You are registered as a user in Template manager <p>Login: %s <p>Password: %s <p> <p><a href=%s>Please, reset your password after 1st-time login</a>";
    private final String MAIL_SUBJECT = "DITO registration";
    private final String FRONT_URL;

    public MailClientImpl(@Value("${spring.mail.host}") final String host,
                          @Value("${spring.mail.port}") final Integer port,
                          @Value("${spring.mail.username}") final String username,
                          @Value("${spring.mail.password}") final String password,
                          @Value("${spring.mail.properties.mail.smtp.auth}") final Boolean auth,
                          @Value("${spring.mail.properties.mail.smtp.starttls.enable}") final Boolean tls,
                          @Value("${spring.mail.front-redirect}") final String frontUrl) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.tls = tls;
        this.FRONT_URL = frontUrl;
    }

    @PostConstruct
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("Mailing is turned on.");
        }

        client = buildMailClient();
    }

    public void sendRegistrationMessage(final String email, final String password) {
        final String mailBody = String.format(MAIL_BODY, email, password, FRONT_URL.concat("/login"));
        send(MAIL_FROM, email, MAIL_SUBJECT, mailBody);
    }

    private void send(final String from, final String to, final String subject, final String text) {
        try {
            final MimeMessage mail = buildMailClient().createMimeMessage();
            mail.setSubject(subject);
            final MimeMessageHelper helper = new MimeMessageHelper(mail);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(text, true);
            client.send(mail);
        } catch (MessagingException e) {
            log.error("Failed to send message. Exception:");
        }
    }

    private JavaMailSenderImpl buildMailClient() {
        final JavaMailSenderImpl client = new JavaMailSenderImpl();
        client.setHost(host);
        client.setPort(port);
        client.setUsername(username);
        client.setPassword(password);

        Properties props = client.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", tls);
        props.put("mail.debug", log.isDebugEnabled());

        return client;
    }
}
