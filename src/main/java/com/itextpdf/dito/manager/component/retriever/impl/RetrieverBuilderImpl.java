package com.itextpdf.dito.manager.component.retriever.impl;

import com.itextpdf.dito.manager.component.retriever.RetrieverBuilder;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.exception.template.TemplateUuidNotFoundException;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.resource.ResourceFileService;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;

@Component
public class RetrieverBuilderImpl implements RetrieverBuilder {

    private final ResourceFileService resourceFileService;
    private final TemplateRepository templateRepository;

    public RetrieverBuilderImpl(final ResourceFileService resourceFileService,
                                final TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
        this.resourceFileService = resourceFileService;
    }

    @Override
    public TemplateAssetRetriever buildTemplateAssetRetriever(final TemplateFileEntity templateFileEntity) {
        return new TemplateAssetRetriever() {
            @Override
            public InputStream getResourceAsStream(final String templateId) {
                final String templateUuid = StringUtils.substringAfter(templateId, DITO_ASSET_TAG);
                final TemplateFileEntity fileEntity = templateFileEntity.getParts().stream()
                        .map(TemplateFilePartEntity::getPart).filter(part -> Objects.equals(part.getTemplate().getUuid(), templateUuid)).findFirst()
                        .orElse(templateRepository.findByUuid(templateUuid).map(TemplateEntity::getLatestFile).orElseThrow(() -> new TemplateUuidNotFoundException(templateUuid)));
                return new ByteArrayInputStream(fileEntity.getData());
            }

            @Override
            public boolean isUriSupported(final String url) {
                return (url != null && url.startsWith(DITO_ASSET_TAG));
            }
        };
    }

    @Override
    public TemplateAssetRetriever buildResourceAssetRetriever(final TemplateFileEntity templateFileEntity) {
        return new TemplateAssetRetriever() {
            @Override
            public InputStream getResourceAsStream(final String resourceId) {
                final String decodedId = StringUtils.substringAfter(resourceId, DITO_ASSET_TAG);

                final ResourceFileEntity fileEntity = templateFileEntity.getResourceFiles().stream()
                        .filter(file -> Objects.equals(file.getResource().getUuid(), decodedId) || Objects.equals(file.getUuid(), decodedId))
                        .findFirst()
                        .orElseGet(() -> getDefaultResourceFileEntity(decodedId));
                return new ByteArrayInputStream(fileEntity.getFile());
            }

            @Override
            public boolean isUriSupported(final String url) {
                return (url != null && url.startsWith(DITO_ASSET_TAG));
            }
        };
    }

    private ResourceFileEntity getDefaultResourceFileEntity(final String uuid) {
        return resourceFileService.getByUuid(uuid);
    }

}
