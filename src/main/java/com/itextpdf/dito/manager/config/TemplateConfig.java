package com.itextpdf.dito.manager.config;

import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateUuidNotFoundException;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.resource.ResourceFileService;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;

@Configuration
public class TemplateConfig {

    private final TemplateRepository templateRepository;
    private final ResourceFileService resourceFileService;

    public TemplateConfig(final TemplateRepository templateRepository,
                          final ResourceFileService resourceFileService) {
        this.templateRepository = templateRepository;
        this.resourceFileService = resourceFileService;
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
            public InputStream getResourceAsStream(final String templateId) {
                final String templateUuid = StringUtils.substringAfter(templateId, DITO_ASSET_TAG);
                final TemplateEntity templateEntity = templateRepository.findByUuid(templateUuid).orElseThrow(() -> new TemplateUuidNotFoundException(templateUuid));
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
                final String decodedId = StringUtils.substringAfter(resourceId, DITO_ASSET_TAG);
                final ResourceFileEntity fileEntity = resourceFileService.getByUuid(decodedId);
                return new ByteArrayInputStream(fileEntity.getFile());
            }

            @Override
            public boolean isUriSupported(final String url) {
                return (url != null && url.startsWith(DITO_ASSET_TAG));
            }
        };
    }

}
