package com.itextpdf.dito.manager.component.retriever.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.retriever.RetrieverBuilder;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;

@Component
public class RetrieverBuilderImpl implements RetrieverBuilder {

    private final ResourceRepository resourceRepository;
    private final TemplateRepository templateRepository;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;
    private final Encoder encoder;

    public RetrieverBuilderImpl(final ResourceRepository resourceRepository,
                                final TemplateRepository templateRepository,
                                final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper,
                                final Encoder encoder) {
        this.resourceRepository = resourceRepository;
        this.templateRepository = templateRepository;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
        this.encoder = encoder;
    }

    @Override
    public TemplateAssetRetriever buildTemplateAssetRetriever(final TemplateFileEntity templateFileEntity) {
        return new TemplateAssetRetriever() {
            @Override
            public InputStream getResourceAsStream(final String resourceId) {
                final String templateName = getIdFromTag(resourceId);
                final TemplateFileEntity fileEntity = templateFileEntity.getParts().stream()
                        .map(TemplateFilePartEntity::getPart).filter(part -> Objects.equals(part.getTemplate().getName(), templateName)).findFirst()
                        .orElse(templateRepository.findByName(templateName).map(TemplateEntity::getLatestFile).orElseThrow(() -> new TemplateNotFoundException(templateName)));
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
                final String decodedId = getIdFromTag(resourceId);
                final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.deserialize(decodedId);
                if (resourceIdDTO == null) {
                    throw new ResourceNotFoundException(resourceId);
                }
                final ResourceFileEntity fileEntity = templateFileEntity.getResourceFiles().stream()
                        .filter(file -> Objects.equals(file.getResource().getName(), resourceIdDTO.getName()))
                        .filter(file -> Objects.equals(file.getResource().getType(), resourceIdDTO.getType()))
                        .filter(file -> isFontTypeEquals(file, resourceIdDTO.getSubName())).findFirst()
                        .orElseGet(() -> getDefaultResourceFileEntity(resourceIdDTO.getName(), resourceIdDTO.getType(), resourceIdDTO.getSubName()));
                return new ByteArrayInputStream(fileEntity.getFile());
            }

            @Override
            public boolean isUriSupported(final String url) {
                return (url != null && url.startsWith(DITO_ASSET_TAG));
            }
        };
    }

    private ResourceFileEntity getDefaultResourceFileEntity(final String resourceName, final ResourceTypeEnum resourceType, final String resourceSubName) {
        return resourceRepository.findByNameAndType(resourceName, resourceType).map(ResourceEntity::getLatestFile)
                .stream().flatMap(Collection::stream)
                .filter(file -> isFontTypeEquals(file, resourceSubName))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException(resourceName));
    }

    private boolean isFontTypeEquals(final ResourceFileEntity fileEntity, final String subName) {
        return subName == null || Objects.equals(fileEntity.getFontName(), subName);
    }

    private String getIdFromTag(final String resourceId) {
        final String encodedId = StringUtils.substringAfter(resourceId, DITO_ASSET_TAG);
        return encoder.decode(encodedId);
    }

}
