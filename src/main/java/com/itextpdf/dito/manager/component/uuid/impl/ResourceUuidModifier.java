package com.itextpdf.dito.manager.component.uuid.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.template.LinksUpdateComponent;
import com.itextpdf.dito.manager.component.uuid.UuidModifier;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.impl.TemplateFileLinkRenamingContext;
import com.itextpdf.dito.sdk.core.preprocess.common.FontResourceUriInternalStyleTagProcessor;
import com.itextpdf.dito.sdk.core.preprocess.common.StylesheetLinkTagProcessor;
import com.itextpdf.dito.sdk.core.process.ProjectImmutableItemProcessor;
import com.itextpdf.dito.sdk.core.process.template.TemplateSubTreeProcessor;
import com.itextpdf.dito.sdk.core.process.template.calculation.CalculationExpressionPrimitiveProcessor;
import com.itextpdf.dito.sdk.core.process.template.css.CssDeclarationValueProcessor;
import com.itextpdf.dito.sdk.core.process.template.css.CssPropertyValueUriProcessor;
import com.itextpdf.dito.sdk.core.process.template.css.CssStatementDeclarationAndRuleSetProcessor;
import com.itextpdf.dito.sdk.core.process.template.css.CssStyleSheetStatementProcessor;
import com.itextpdf.dito.sdk.core.process.template.node.ImageTagUriProcessor;
import com.itextpdf.dito.sdk.core.process.template.node.NodeCalculatedStyleAttributePropertyProcessor;
import com.itextpdf.dito.sdk.core.process.template.node.NodeStyleAttributeContentProcessor;
import com.itextpdf.dito.sdk.core.process.template.node.StyleTagContentProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ResourceUuidModifier implements UuidModifier {
    private static final Logger log = LogManager.getLogger(ResourceUuidModifier.class);

    private final ResourceRepository resourceRepository;
    private final LinksUpdateComponent linksUpdateComponent;
    private final TemplateRepository templateRepository;
    private final TemplateFileRepository templateFileRepository;
    private final Encoder encoder;
    private final ObjectMapper objectMapper;

    public ResourceUuidModifier(final ResourceRepository resourceRepository,
                                final TemplateRepository templateRepository,
                                final TemplateFileRepository templateFileRepository,
                                final LinksUpdateComponent linksUpdateComponent,
                                final Encoder encoder,
                                final ObjectMapper objectMapper) {
        this.resourceRepository = resourceRepository;
        this.templateRepository = templateRepository;
        this.templateFileRepository = templateFileRepository;
        this.linksUpdateComponent = linksUpdateComponent;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getTarget() {
        return "RESOURCE";
    }

    @Override
    public void updateEmptyUuid() {
        final List<ResourceEntity> nullUuidResourceList = resourceRepository.findByUuidNull();

        for (final ResourceEntity resourceEntity : nullUuidResourceList) {
            updateLinksToUuid(resourceEntity);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    private void updateLinksToUuid(ResourceEntity resourceEntity) {
        resourceEntity.setUuid(UUID.randomUUID().toString());
        updateResourceLinksInTemplates(resourceEntity);
        resourceRepository.save(resourceEntity);
    }

    @PostConstruct
    public void onInit() {
        updateEmptyUuid();
    }

    private void updateResourceLinksInTemplates(final ResourceEntity resourceEntity) {
        final List<TemplateFileEntity> templateFiles = templateRepository.findTemplateFilesByResourceId(resourceEntity.getId());
        if (!templateFiles.isEmpty()) {
            final List<String> oldIdList;
            final List<String> newIdList;
            if (ResourceTypeEnum.FONT.equals(resourceEntity.getType())) {
                oldIdList = new ArrayList<>();
                newIdList = new ArrayList<>();
                oldIdList.add(encodeId(resourceEntity.getName(), resourceEntity.getType(), null));
                newIdList.add(resourceEntity.getUuid());
                for (final ResourceFileEntity fileEntity : resourceEntity.getLatestFile()) {
                    oldIdList.add(encodeId(resourceEntity.getName(), resourceEntity.getType(), fileEntity.getFontName()));
                    newIdList.add(fileEntity.getUuid());
                }
            } else {
                oldIdList = Collections.singletonList(encodeId(resourceEntity.getName(), resourceEntity.getType(), null));
                newIdList = Collections.singletonList(resourceEntity.getUuid());
            }

            for (int i = 0; i < oldIdList.size(); i++) {
                final TemplateSubTreeProcessor<Node, TemplateFileLinkRenamingContext> processor = getTemplateSubTreeProcessor(oldIdList.get(i), newIdList.get(i));
                log.info("Replace resource links: {} for: {}", oldIdList.get(i), newIdList.get(i));
                linksUpdateComponent.updateLinksInFiles(templateFiles, oldIdList.get(i), newIdList.get(i), processor);
            }
            templateFileRepository.saveAll(templateFiles);
        }
    }

    private TemplateSubTreeProcessor<Node, TemplateFileLinkRenamingContext> getTemplateSubTreeProcessor(String oldId, String newId) {
        final TemplateFileLinkRenamingContext templateFileLinkRenamingContext = new TemplateFileLinkRenamingContext(oldId, newId);
        final ProjectImmutableItemProcessor<String, TemplateFileLinkRenamingContext> renamingUrlProcessor = templateFileLinkRenamingContext.getRenamingUrlProcessor();
        return new TemplateSubTreeProcessor<>(
                    Arrays.asList(
                            new ImageTagUriProcessor<>(renamingUrlProcessor),
                            new NodeCalculatedStyleAttributePropertyProcessor<>(
                                    new CalculationExpressionPrimitiveProcessor<>(
                                            new CssPropertyValueUriProcessor<>(
                                                    renamingUrlProcessor
                                            )
                                    )
                            ),
                            new StyleTagContentProcessor<>(
                                    new CssStyleSheetStatementProcessor<>(
                                            new CssStatementDeclarationAndRuleSetProcessor<>(
                                                    new CssDeclarationValueProcessor<>(
                                                            new CssPropertyValueUriProcessor<>(
                                                                    renamingUrlProcessor
                                                            )
                                                    )
                                            )
                                    )
                            ),
                            new NodeStyleAttributeContentProcessor<>(
                                    new CssDeclarationValueProcessor<>(
                                            new CssPropertyValueUriProcessor<>(
                                                    renamingUrlProcessor
                                            )
                                    )
                            ),
                            new FontResourceUriInternalStyleTagProcessor<>(renamingUrlProcessor),
                            new StylesheetLinkTagProcessor<>(renamingUrlProcessor)
                    )
        );
    }

    private String encodeId(final String name, final ResourceTypeEnum resourceTypeEnum, final String subName) {
        final ResourceIdDTO resourceIdDTO = new ResourceIdDTO();
        resourceIdDTO.setName(name);
        resourceIdDTO.setType(resourceTypeEnum);
        resourceIdDTO.setSubName(subName);
        final String json = serialize(resourceIdDTO);

        return Optional.ofNullable(json).map(encoder::encode).orElse("");
    }

    private String serialize(final Object data) {
        String result = null;

        try {
            result = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e);
        }

        return result;
    }
}
