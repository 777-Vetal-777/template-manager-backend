package com.itextpdf.dito.manager.component.uuid.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.template.LinksUpdateComponent;
import com.itextpdf.dito.manager.component.uuid.UuidModifier;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.impl.TemplateFileLinkRenamingContext;
import com.itextpdf.dito.sdk.core.preprocess.common.FragmentLinkProcessor;
import com.itextpdf.dito.sdk.core.process.ProjectImmutableItemProcessor;
import com.itextpdf.dito.sdk.core.process.template.TemplateSubTreeProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Node;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class TemplateUuidModifier implements UuidModifier {

    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final LinksUpdateComponent linksUpdateComponent;
    private final Encoder encoder;

    public TemplateUuidModifier(final TemplateFileRepository templateFileRepository,
                                final TemplateRepository templateRepository,
                                final Encoder encoder,
                                final LinksUpdateComponent linksUpdateComponent) {
        this.templateFileRepository = templateFileRepository;
        this.templateRepository = templateRepository;
        this.linksUpdateComponent = linksUpdateComponent;
        this.encoder = encoder;
    }

    @Override
    public String getTarget() {
        return DependencyType.TEMPLATE.toString();
    }

    @Override
    public void updateEmptyUuid() {
        updateEmptyUuidForTemplateFiles();
        updateEmptyUuidForTemplates();
    }

    private void updateEmptyUuidForTemplateFiles() {
        final List<TemplateFileEntity> nullUuidTemplateFiles = templateFileRepository.findByUuidNull();

        for (TemplateFileEntity templateFileEntity : nullUuidTemplateFiles) {
            templateFileEntity.setUuid(UUID.randomUUID().toString());
        }

        templateFileRepository.saveAll(nullUuidTemplateFiles);
    }

    private void updateEmptyUuidForTemplates() {
        final List<TemplateEntity> nullUuidTemplates = templateRepository.findByUuidNull();

        for (final TemplateEntity templateEntity : nullUuidTemplates) {
            updateLinkToUuid(templateEntity);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    private void updateLinkToUuid(TemplateEntity templateEntity) {
        templateEntity.setUuid(UUID.randomUUID().toString());
        updateTemplateLinksInTemplates(templateEntity);
        templateRepository.save(templateEntity);
    }

    private void updateTemplateLinksInTemplates(final TemplateEntity templateEntity) {
        final List<TemplateFileEntity> nestedTemplates = templateRepository.getAllTemplateFileVersions(templateEntity.getId());
        if (!nestedTemplates.isEmpty()) {
            final String oldId = encoder.encode(templateEntity.getName());
            final String newId = templateEntity.getUuid();
            final TemplateFileLinkRenamingContext templateFileLinkRenamingContext = new TemplateFileLinkRenamingContext(oldId, newId);
            final ProjectImmutableItemProcessor<String, TemplateFileLinkRenamingContext> renamingUrlProcessor = templateFileLinkRenamingContext.getRenamingUrlProcessor();
            final TemplateSubTreeProcessor<Node, TemplateFileLinkRenamingContext> processor = new TemplateSubTreeProcessor<>(Collections.singletonList(new FragmentLinkProcessor<>(renamingUrlProcessor)));
            linksUpdateComponent.updateLinksInFiles(nestedTemplates, oldId, newId, processor);
            templateFileRepository.saveAll(nestedTemplates);
        }
    }

    @PostConstruct
    public void onInit() {
        updateEmptyUuid();
    }
}
