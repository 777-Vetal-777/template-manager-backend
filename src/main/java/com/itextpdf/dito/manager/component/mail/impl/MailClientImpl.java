package com.itextpdf.dito.manager.component.mail.impl;

import com.itextpdf.dito.manager.component.mail.MailClient;

import java.util.Properties;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "DITO_MANAGER_MAILING_ENABLE", havingValue = "true")
public class MailClientImpl implements MailClient {
    private static final Logger log = LogManager.getLogger(MailClientImpl.class);

    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    private final Boolean auth;
    private final Boolean tls;

    private JavaMailSender client;

    public MailClientImpl(@Value("${spring.mail.host}") final String host,
            @Value("${spring.mail.port}") final Integer port,
            @Value("${spring.mail.username}") final String username,
            @Value("${spring.mail.password}") final String password,
            @Value("${spring.mail.properties.mail.smtp.auth}") final Boolean auth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable}") final Boolean tls) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.tls = tls;
    }

    @PostConstruct
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("Mailing is turned on.");
        }

        client = buildMailClient();
    }

    @Override
    public void send(final String from, final String to, final String subject, final String text) {
        final SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);
        client.send(mail);
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
