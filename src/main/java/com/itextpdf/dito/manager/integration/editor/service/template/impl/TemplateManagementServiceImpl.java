package com.itextpdf.dito.manager.integration.editor.service.template.impl;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.sdk.core.dependency.api.TemplateDependency;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.DefaultTemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.TemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateManagementServiceImpl implements TemplateManagementService {
    private final TemplateService templateService;

    public TemplateManagementServiceImpl(final TemplateService templateService) {
        this.templateService = templateService;
    }


    @Override
    public TemplateEntity get(final String name) {
        return templateService.get(name);
    }

    @Override
    public List<TemplateEntity> getAll() {
        return templateService.getAll();
    }

    @Override
    public TemplateEntity createNewVersion(final String name, final byte[] data, final String email,
            final String newName) {
        return templateService.createNewVersion(name, data, email, null, newName);
    }

    @Override
    public TemplateEntity create(final String name, final String email) {
        return templateService.create(name, TemplateTypeEnum.STANDARD, null, email);
    }

    @Override
    public TemplateEntity delete(final String templateName) {
        return templateService.delete(templateName);
    }

    private void provideConsistency(final byte[] data) {
        final TemplateDependenciesRetriever retriever = new DefaultTemplateDependenciesRetriever(
                templateAssetRetriever, resourceAssetRetriever);
        try {
            final List<TemplateDependency> dependencies = retriever.getDependencies(new ByteArrayInputStream(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TemplateAssetRetriever templateAssetRetriever = new TemplateAssetRetriever() {
        @Override
        public InputStream getResourceAsStream(String s) throws IOException {
            return null;
        }

        @Override
        public boolean isUriSupported(String s) {
            return false;
        }
    };

    private static TemplateAssetRetriever resourceAssetRetriever = new TemplateAssetRetriever() {
        @Override
        public InputStream getResourceAsStream(String s) throws IOException {
            return null;
        }

        @Override
        public boolean isUriSupported(String s) {
            return false;
        }
    };
}
