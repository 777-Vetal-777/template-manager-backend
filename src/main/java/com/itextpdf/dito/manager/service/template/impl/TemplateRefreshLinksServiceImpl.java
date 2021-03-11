package com.itextpdf.dito.manager.service.template.impl;

import com.google.common.base.Charsets;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.template.TemplateUpdatingOutdatedLinkException;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateDescriptorMapper;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateRefreshLinksService;
import com.itextpdf.dito.sdk.core.preprocess.common.FontResourceUriInternalStyleTagProcessor;
import com.itextpdf.dito.sdk.core.preprocess.common.FragmentLinkProcessor;
import com.itextpdf.dito.sdk.core.preprocess.common.StylesheetLinkTagProcessor;
import com.itextpdf.dito.sdk.core.process.MutableItemProcessingResult;
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
import com.itextpdf.dito.sdk.internal.core.template.parser.impl.jsoup.JsoupDocument;
import com.itextpdf.dito.sdk.internal.core.template.parser.impl.jsoup.JsoupTemplateParser;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class TemplateRefreshLinksServiceImpl implements TemplateRefreshLinksService {
    private final TemplateRepository templateRepository;
    private final TemplateFileRepository templateFileRepository;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;
    private final TemplateDescriptorMapper templateDescriptorMapper;

    public TemplateRefreshLinksServiceImpl(final TemplateRepository templateRepository,
                                           final TemplateFileRepository templateFileRepository,
                                           final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper,
                                           final TemplateDescriptorMapper templateDescriptorMapper) {
        this.templateRepository = templateRepository;
        this.templateFileRepository = templateFileRepository;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
        this.templateDescriptorMapper = templateDescriptorMapper;
    }

    @Override
    public void updateResourceLinksInTemplates(final ResourceEntity existingResource, final String newName) {
        final List<TemplateFileEntity> templateFiles = templateRepository.findTemplateFilesByResourceId(existingResource.getId());
        if (!templateFiles.isEmpty()) {
            final String oldId = resourceLeafDescriptorMapper.encodeId(existingResource.getName(), existingResource.getType(), null);
            final String newId = resourceLeafDescriptorMapper.encodeId(newName, existingResource.getType(), null);
            final TemplateFileLinkRenamingContext templateFileLinkRenamingContext = new TemplateFileLinkRenamingContext(oldId, newId);
            final ProjectImmutableItemProcessor<String, TemplateFileLinkRenamingContext> renamingUrlProcessor = templateFileLinkRenamingContext.getRenamingUrlProcessor();
            TemplateSubTreeProcessor<TemplateFileLinkRenamingContext> processor;
            if (existingResource.getType() == ResourceTypeEnum.IMAGE) {
                processor = new TemplateSubTreeProcessor<>(
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
                                        ))
                        ));
            }
            else if (existingResource.getType() == ResourceTypeEnum.FONT) {
                processor = new TemplateSubTreeProcessor<>(Collections.singletonList(
                        new FontResourceUriInternalStyleTagProcessor<>(renamingUrlProcessor))
                );
            } else {
                processor =  new TemplateSubTreeProcessor<>(Collections.singletonList(new StylesheetLinkTagProcessor<>(renamingUrlProcessor)));
            }
            updateOldLinksInFiles(templateFiles, oldId, newId, processor);
            templateFileRepository.saveAll(templateFiles);
        }
    }

    @Override
    public void updateTemplateLinksInTemplates(final TemplateEntity templateEntity, final String newName) {
        final List<TemplateFileEntity> nestedTemplates = templateRepository.getAllTemplateFileVersions(templateEntity.getId());
        if (!nestedTemplates.isEmpty()) {
            final String oldId = templateDescriptorMapper.encodeToBase64(templateEntity.getName());
            final String newId = templateDescriptorMapper.encodeToBase64(newName);
            final TemplateFileLinkRenamingContext templateFileLinkRenamingContext = new TemplateFileLinkRenamingContext(oldId, newId);
            final ProjectImmutableItemProcessor<String, TemplateFileLinkRenamingContext> renamingUrlProcessor = templateFileLinkRenamingContext.getRenamingUrlProcessor();
            final TemplateSubTreeProcessor<TemplateFileLinkRenamingContext> processor = new TemplateSubTreeProcessor<>(Collections.singletonList(new FragmentLinkProcessor<>(renamingUrlProcessor)));
            updateOldLinksInFiles(nestedTemplates, oldId, newId, processor);
            templateFileRepository.saveAll(nestedTemplates);
        }
    }

    private void updateOldLinksInFiles(final List<TemplateFileEntity> templateFiles, final String oldId, final String newId, final TemplateSubTreeProcessor<TemplateFileLinkRenamingContext> processor) {
        templateFiles.forEach(file -> {
            try {
                final JsoupDocument template = JsoupTemplateParser.parse(new ByteArrayInputStream(file.getData()), null, "");
                final MutableItemProcessingResult result = processor.process(template, new TemplateFileLinkRenamingContext(oldId, newId));
                if (result.isModified()) {
                    file.setData(template.outerHtml().getBytes(Charsets.UTF_8));
                }
            } catch (Exception exception) {
                throw new TemplateUpdatingOutdatedLinkException();
            }
        });
    }
}