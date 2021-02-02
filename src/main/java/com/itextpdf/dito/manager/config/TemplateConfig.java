package com.itextpdf.dito.manager.config;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfig {

    private final ResourceRepository resourceRepository;
    private final TemplateRepository templateRepository;
    private final ResourceMapper resourceMapper;

    public TemplateConfig(final ResourceRepository resourceRepository,
            TemplateRepository templateRepository,
            ResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.templateRepository = templateRepository;
        this.resourceMapper = resourceMapper;
    }

    /**
     * Bean for linking template resources within PDF generation process.
     *
     * @return template asset retriever component
     */
    @Bean
    public TemplateAssetRetriever templateAssetRetriever() {
        return new TemplateAssetRetriever() {
            @Override
            public InputStream getResourceAsStream(final String resourceId) {
                final String templateName = getIdFromTag(resourceId);
                final TemplateEntity templateEntity = templateRepository.findByName(templateName)
                        .orElseThrow(() -> new TemplateNotFoundException(templateName));
                return new ByteArrayInputStream(templateEntity.getLatestFile().getData());
            }

            @Override
            public boolean isUriSupported(final String url) {
                return url.startsWith("dito-asset://");
            }
        };
    }

    @Bean
    public TemplateAssetRetriever resourceAssetRetriever() {
        return new TemplateAssetRetriever() {
            @Override
            public InputStream getResourceAsStream(final String resourceId) {
                final String decodedId = getIdFromTag(resourceId);
                final ResourceIdDTO resourceIdDTO = resourceMapper.deserialize(decodedId);
                final ResourceEntity resourceEntity = resourceRepository
                        .findByNameAndType(resourceIdDTO.getName(), resourceIdDTO.getType())
                        .orElseThrow(() -> new ResourceNotFoundException(resourceIdDTO.getName()));
                return new ByteArrayInputStream(resourceEntity.getLatestFile().get(0).getFile());
            }

            @Override
            public boolean isUriSupported(final String url) {
                return url.startsWith("dito-asset://");
            }
        };
    }

    @NotNull
    private String getIdFromTag(final String resourceId) {
        final String cuttedId = StringUtils.substringAfter(resourceId, "dito-asset://");
        return resourceMapper.decode(cuttedId);
    }
}
