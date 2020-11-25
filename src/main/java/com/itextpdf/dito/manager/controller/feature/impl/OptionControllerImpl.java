package com.itextpdf.dito.manager.controller.feature.impl;

import com.itextpdf.dito.manager.component.mail.MailClient;
import com.itextpdf.dito.manager.controller.feature.OptionController;
import com.itextpdf.dito.manager.dto.option.OptionsDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OptionControllerImpl implements OptionController {
    private MailClient mailSender;

    @Override
    public ResponseEntity<OptionsDTO> get() {
        return new ResponseEntity<>(new OptionsDTO(mailSender != null), HttpStatus.OK);
    }

    @Autowired(required = false)
    public void setMailSender(final MailClient mailSender) {
        this.mailSender = mailSender;
    }
}
