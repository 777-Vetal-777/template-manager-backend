package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.service.template.TemplateLoader;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DefaultTemplateLoader implements TemplateLoader {
    private byte[] data;

    @PostConstruct
    public void init() throws IOException {
        data = this.getClass().getClassLoader().getResourceAsStream("templates/default.html").readAllBytes();
    }

    @Override
    public byte[] load() {
        return data;
    }
}
