package com.itextpdf.dito.manager.util.cssimport;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.exception.resource.ResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.sdk.core.process.MutableItemProcessingResult;
import com.itextpdf.dito.sdk.core.process.ProjectMutableItemProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Element;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import static com.itextpdf.dito.manager.util.cssimport.StyleSheetTagConstants.DITO_ASSET_HREF;
import static com.itextpdf.html2pdf.html.AttributeConstants.REL;
import static com.itextpdf.html2pdf.html.AttributeConstants.HREF;
import static com.itextpdf.html2pdf.html.AttributeConstants.STYLESHEET;
import static com.itextpdf.html2pdf.html.TagConstants.LINK;

@Service
public class StyleSheetPreprocessorService implements ProjectMutableItemProcessor<Element, StyleTagRenamingContext> {

    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;

    public StyleSheetPreprocessorService(final ResourceService resourceService,
                                         final ResourceRepository resourceRepository,
                                         final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper) {
        this.resourceService = resourceService;
        this.resourceRepository = resourceRepository;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
    }

    @Override
    public MutableItemProcessingResult process(final Element element, final StyleTagRenamingContext context) {
        final MutableItemProcessingResult.Builder result = MutableItemProcessingResult.unmodified();
        final String originalName = element.attr(StyleSheetTagConstants.STYLESHEET_NAME);

        try {
                final ResourceTypeEnum type = context.getType();
            importStyleSheet(element, context, originalName, type);
            element.attr(REL,STYLESHEET);
            element.tagName(LINK);
            element.html("");
            result.setModified(true);

        } catch (ResourceAlreadyExistsException e) {
            context.getDuplicatesList().putToDuplicates(SettingType.STYLESHEET, originalName);
        }

        return result.build();
    }

    private void importStyleSheet(final Element element,
                                  final StyleTagRenamingContext context,
                                  final String originalName,
                                  final ResourceTypeEnum type) {
        try {
            resourceService.get(originalName, type);

            if (!context.getRenamingSettings().containsKey(originalName)) {
                throw new ResourceAlreadyExistsException(originalName);
            }

            if (Boolean.TRUE.equals(context.getRenamingSettings().get(originalName).getAllowedNewVersion())) {
                final String resourceDitoId = DITO_ASSET_HREF.concat(resourceLeafDescriptorMapper.encodeId(originalName, type, null));
                element.attr(HREF, resourceDitoId);

                resourceService.createNewVersion(originalName, context.getType(), element.html().getBytes(StandardCharsets.UTF_8), originalName, context.getEmail(), "Import template");
            } else {
                final AtomicLong resourceId = new AtomicLong(resourceRepository.findMaxIntegerByNamePattern(context.getFileName()).orElse(0));
                final String resourceName = new StringBuilder(context.getFileName()).append("(").append(resourceId.incrementAndGet()).append(")").toString();
                final String resourceDitoId = DITO_ASSET_HREF.concat(resourceLeafDescriptorMapper.encodeId(resourceName, type, null));
                element.attr(HREF, resourceDitoId);

                resourceService.create(resourceName, context.getType(), element.html().getBytes(StandardCharsets.UTF_8), originalName, context.getEmail());
            }

        } catch (ResourceNotFoundException e) {
            final String resourceDitoId = DITO_ASSET_HREF.concat(resourceLeafDescriptorMapper.encodeId(originalName, type, null));
            element.attr(HREF, resourceDitoId);

            resourceService.create(originalName, context.getType(), element.html().getBytes(StandardCharsets.UTF_8), originalName, context.getEmail());
        }
    }
}
