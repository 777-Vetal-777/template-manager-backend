package com.itextpdf.dito.manager.config;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;

@Configuration
public class TemplateConfig {

    private final ResourceRepository resourceRepository;
    private final TemplateRepository templateRepository;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;
    private final Encoder encoder;

    public TemplateConfig(final ResourceRepository resourceRepository,
                          final TemplateRepository templateRepository,
                          final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper,
                          final Encoder encoder) {
        this.resourceRepository = resourceRepository;
        this.templateRepository = templateRepository;
        this.encoder = encoder;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
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
                return (url != null && url.startsWith(DITO_ASSET_TAG));
            }
        };
    }

    @Bean
    public TemplateAssetRetriever resourceAssetRetriever() {
        return new TemplateAssetRetriever() {
            @Override
            public InputStream getResourceAsStream(final String resourceId) {
                final String decodedId = getIdFromTag(resourceId);
                final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.deserialize(decodedId);
                final ResourceEntity resourceEntity = resourceRepository
                        .findByNameAndType(resourceIdDTO.getName(), resourceIdDTO.getType())
                        .orElseThrow(() -> new ResourceNotFoundException(resourceIdDTO.getName()));
                return new ByteArrayInputStream(resourceEntity.getLatestFile().get(0).getFile());
            }

            @Override
            public boolean isUriSupported(final String url) {
                return (url != null && url.startsWith(DITO_ASSET_TAG));
            }
        };
    }

    private String getIdFromTag(final String resourceId) {
        final String cuttedId = StringUtils.substringAfter(resourceId, DITO_ASSET_TAG);
        return encoder.decode(cuttedId);
    }
}
