package com.itextpdf.dito.manager.component.mail.impl;

import com.itextpdf.dito.manager.component.mail.MailClient;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.mail.MailingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.ArrayList;
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

    private static final String MAIL_SUBJECT = "DITO registration";
    private static final String MAIL_PASSWORD_WAS_UPDATED_BY_ADMIN_SUBJECT = "DITO password was updated by admin";
    private static final String MAIL_RESET_PASSWORD_SUBJECT = "DITO reset password";
    private final String frontURL;
    private final String privacyInformationUrl;

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
        this.frontURL = frontUrl;
        this.privacyInformationUrl = privacyInformation;
    }

    @PostConstruct
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("Mailing is turned on.");
        }
        client = buildMailClient();
    }

    @Override
    public void sendRegistrationMessage(final UserEntity savedUser, final String password, final UserEntity currentUser) {
        log.info("Send registration message for user: {} was started", savedUser);
        final String mailBody = generateRegistrationHtml(savedUser, password, currentUser);
        try {
            send(username, savedUser.getEmail(), MAIL_SUBJECT, mailBody);
            log.info("Send registration message for user: {} was finished successfully", savedUser);
        } catch (Exception ex) {
            throw new MailingException(ex.getMessage());
        }
    }

    @Override
    public void sendPasswordsWasUpdatedByAdminMessage(final UserEntity savedUser, final String password, final UserEntity admin) {
        final String mailBody = generatePasswordUpdatedByAdminHtml(savedUser, admin, password);
        try {
            send(username, savedUser.getEmail(), MAIL_PASSWORD_WAS_UPDATED_BY_ADMIN_SUBJECT, mailBody);
        } catch (Exception ex) {
            throw new MailingException(ex.getMessage());
        }
    }

    @Override
    public void sendResetMessage(final UserEntity userEntity, final String token) {
        log.info("Sen reset password for user: {} was started", userEntity);
        final String mailBody = generateResetPasswordHtml(userEntity, token);
        try {
            send(username, userEntity.getEmail(), MAIL_RESET_PASSWORD_SUBJECT, mailBody);
            log.info("Sen reset password for user: {} was finished successfully", userEntity);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new MailingException(e.getMessage());
        }
    }

    private String generateResetPasswordHtml(final UserEntity userEntity, final String token) {
        log.info("Generate reset password html for user: {} was started", userEntity);
        final List<String> list = readFile("templates/resetPasswordEmail.html");
        list.set(26, String.format(list.get(26), userEntity.getFirstName() + " " + userEntity.getLastName()));
        list.set(35, String.format(list.get(35), frontURL.concat("/forgot_password?token=").concat(token)));
        list.set(48, String.format(list.get(48), Year.now().getValue()));
        list.set(51, String.format(list.get(51), privacyInformationUrl));
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String str2 : list) {
            stringBuilder.append(str2);
        }
        return stringBuilder.toString();
    }

    private String generatePasswordUpdatedByAdminHtml(final UserEntity userEntity, final UserEntity admin, final String password) {
        final List<String> list = readFile("templates/passwordUpdatedByAdminEmail.html");
        list.set(26, String.format(list.get(26), userEntity.getFirstName() + " " + userEntity.getLastName()));
        list.set(29, String.format(list.get(29), admin.getFirstName() + " "+ admin.getLastName()));
        list.set(36, String.format(list.get(36), userEntity.getEmail()));
        list.set(40, String.format(list.get(40), password));
        list.set(44, String.format(list.get(44), frontURL.concat("/login")));
        list.set(60, String.format(list.get(60), privacyInformationUrl));
        list.set(57, String.format(list.get(57), Year.now().getValue()));
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String str2 : list) {
            stringBuilder.append(str2);
        }
        log.info("Generate reset password for user: {} was finished successfully", userEntity);
        return stringBuilder.toString();
    }

    private String generateRegistrationHtml(final UserEntity userEntity, final String password, final UserEntity currentUser) {
        log.info("Generate registration html for user: {} was started", userEntity);
        final List<String> list = readFile("templates/registrationEmail.html");
        list.set(26, String.format(list.get(26), userEntity.getFirstName() + " " + userEntity.getLastName()));
        list.set(29, String.format(list.get(29), currentUser.getFirstName() + " " + currentUser.getLastName()));
        list.set(41, String.format(list.get(41), userEntity.getFirstName()));
        list.set(45, String.format(list.get(45), password));
        list.set(49, String.format(list.get(49), frontURL.concat("/login")));
        list.set(65, String.format(list.get(65), privacyInformationUrl));
        list.set(62, String.format(list.get(62), Year.now().getValue()));
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String str2 : list) {
            stringBuilder.append(str2);
        }
        log.info("Generate registration html for user: {} was finished successfully", userEntity);
        return stringBuilder.toString();
    }

    private List<String> readFile(final String file) {
        log.info("Read file: {} was started", file);
        final InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file));
        final List<String> list = new ArrayList<>();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            log.error("Failed to read html {}", file);
        }
        log.info("Read file: {} was finished successfully", file);
        return list;
    }

    private void send(final String from, final String to, final String subject, final String text)
            throws MessagingException {
        log.info("Send message from: {} to {} was started", from, to);
        final MimeMessage mail = client.createMimeMessage();
        mail.setSubject(subject);
        final MimeMessageHelper helper = new MimeMessageHelper(mail);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(text, true);
        client.send(mail);
        log.info("Send message from: {} to {} was finished successfully", from, to);
    }

    private JavaMailSenderImpl buildMailClient() {
        final JavaMailSenderImpl mailClient = new JavaMailSenderImpl();
        mailClient.setHost(host);
        mailClient.setPort(port);
        mailClient.setUsername(username);
        mailClient.setPassword(password);

        final Properties props = mailClient.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", tls);
        props.put("mail.debug", log.isDebugEnabled());

        return mailClient;
    }
}
