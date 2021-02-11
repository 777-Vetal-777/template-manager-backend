package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.editor.server.common.service.template.defaultprovider.DefaultTemplateProvider;
import com.itextpdf.dito.editor.server.common.service.template.defaultprovider.TemplateProvider;
import com.itextpdf.dito.manager.service.template.TemplateLoader;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SdkTemplateLoader implements TemplateLoader {
    private final TemplateProvider templateProvider = new DefaultTemplateProvider();
    private byte[] data;

    @PostConstruct
    public void init() throws IOException {
        data = templateProvider.provide().readAllBytes();
    }

    @Override
    public byte[] load() {
        return data;
    }
}
