package com.itextpdf.dito.manager.component.mail.impl;

import com.google.common.io.Files;
import com.itextpdf.dito.manager.component.mail.MailClient;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.mail.MailingException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

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

    private static final String MAIL_FROM = "vadzim.sarokin.tech.acc@gmail.com";
    private static final String MAIL_SUBJECT = "DITO registration";
    private static final String MAIL_RESET_PASSWORD_SUBJECT = "DITO reset password";
    private final String FRONT_URL;
    private final String PRIVACY_INFORMATION_URL;

    public MailClientImpl(@Value("${spring.mail.host}") final String host,
                          @Value("${spring.mail.port}") final Integer port,
                          @Value("${spring.mail.username}") final String username,
                          @Value("${spring.mail.password}") final String password,
                          @Value("${spring.mail.properties.mail.smtp.auth}") final Boolean auth,
                          @Value("${spring.mail.properties.mail.smtp.starttls.enable}") final Boolean tls,
                          @Value("${spring.mail.front-redirect}") final String frontUrl,
                          @Value("${spring.mail.privacy-information}") final String privacyInformation) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.tls = tls;
        this.FRONT_URL = frontUrl;
        this.PRIVACY_INFORMATION_URL = privacyInformation;
    }

    @PostConstruct
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("Mailing is turned on.");
        }
        client = buildMailClient();
    }

    public void sendRegistrationMessage(final UserEntity savedUser, final String password, final UserEntity currentUser) {
        final String mailBody = generateRegistrationHtml(savedUser, password, currentUser);
        try {
            send(MAIL_FROM, savedUser.getEmail(), MAIL_SUBJECT, mailBody);
        } catch (Exception ex) {
            throw new MailingException(ex.getMessage());
        }
    }

    @Override
    public void sendResetMessage(final UserEntity userEntity, final String token) {
        final String mailBody = generateResetPasswordHtml(userEntity, token);
        try {
            send(MAIL_FROM, userEntity.getEmail(), MAIL_RESET_PASSWORD_SUBJECT, mailBody);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateResetPasswordHtml(final UserEntity userEntity, final String token) {
        final File file = new File("src/main/resources/templates/resetPasswordEmail.html");
        String message = null;
        try {
            List<String> list = Files.readLines(file, StandardCharsets.UTF_8);
            list.set(26, String.format(list.get(26), userEntity.getFirstName() + " " + userEntity.getLastName()));
            list.set(35, String.format(list.get(35), FRONT_URL.concat("/forgot_password?token=").concat(token)));
            list.set(48, String.format(list.get(48), Year.now().getValue()));
            list.set(51, String.format(list.get(51), PRIVACY_INFORMATION_URL));
            StringBuilder stringBuilder = new StringBuilder();
            for (String str2 : list) {
                stringBuilder.append(str2);
            }
            message = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    private String generateRegistrationHtml(final UserEntity userEntity, final String password, final UserEntity currentUser) {
        final File file = new File("src/main/resources/templates/registrationEmail.html");
        String message = null;
        try {
            List<String> list = Files.readLines(file, StandardCharsets.UTF_8);
            list.set(26, String.format(list.get(26), userEntity.getFirstName() + " " + userEntity.getLastName()));
            list.set(29, String.format(list.get(29), currentUser.getFirstName() + " " + currentUser.getLastName()));
            list.set(41, String.format(list.get(41), userEntity.getFirstName()));
            list.set(45, String.format(list.get(45), password));
            list.set(49, String.format(list.get(49), FRONT_URL.concat("/login")));
            list.set(65, String.format(list.get(65), PRIVACY_INFORMATION_URL));
            list.set(62, String.format(list.get(62), Year.now().getValue()));
            StringBuilder stringBuilder = new StringBuilder();
            for (String str2 : list) {
                stringBuilder.append(str2);
            }
            message = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void send(final String from, final String to, final String subject, final String text)
            throws MessagingException {
        final MimeMessage mail = client.createMimeMessage();
        mail.setSubject(subject);
        final MimeMessageHelper helper = new MimeMessageHelper(mail);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(text, true);
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
