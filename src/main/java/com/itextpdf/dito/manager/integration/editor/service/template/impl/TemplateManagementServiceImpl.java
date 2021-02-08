package com.itextpdf.dito.manager.integration.editor.service.template.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.sdk.core.dependency.api.TemplateDependency;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.DefaultTemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.TemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class TemplateManagementServiceImpl implements TemplateManagementService {
    private final TemplateService templateService;
    private final TemplateAssetRetriever resourceAssetRetriever;
    private final TemplateAssetRetriever templateAssetRetriever;
    private final TemplateRepository templateRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceService resourceService;

    public TemplateManagementServiceImpl(final TemplateService templateService,
            final TemplateAssetRetriever resourceAssetRetriever,
            final TemplateAssetRetriever templateAssetRetriever,
            final TemplateRepository templateRepository,
            final ResourceMapper resourceMapper,
            final ResourceService resourceService) {
        this.templateService = templateService;
        this.resourceAssetRetriever = resourceAssetRetriever;
        this.templateAssetRetriever = templateAssetRetriever;
        this.resourceMapper = resourceMapper;
        this.resourceService = resourceService;
        this.templateRepository = templateRepository;
        
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
		final TemplateEntity templateEntity = templateService.createNewVersion(name, data, email, null, newName, null);
		templateEntity.getLatestFile().setResourceFiles(provideConsistency(templateEntity.getLatestFile().getData()));
		return templateRepository.save(templateEntity);
    }

    @Override
    public TemplateEntity create(final String name, final String email) {
        return templateService.create(name, TemplateTypeEnum.STANDARD, null, email, null);
    }

    @Override
    public TemplateEntity delete(final String templateName) {
        return templateService.delete(templateName);
    }

    private Set<ResourceFileEntity> provideConsistency(final byte[] data) {
		final TemplateDependenciesRetriever retriever = new DefaultTemplateDependenciesRetriever(templateAssetRetriever,
				resourceAssetRetriever);
		final Set<ResourceFileEntity> entitySet = new HashSet<>();
		try {
			final List<TemplateDependency> dependencies = retriever.getDependencies(new ByteArrayInputStream(data));
			for (final TemplateDependency td : dependencies) {
				final String decodedUrl = resourceMapper.decode(td.getUri().toString().replace("dito-asset://", ""));
				final ResourceIdDTO dto = resourceMapper.deserialize(decodedUrl);
				final ResourceEntity resourceEntity = resourceService.get(dto.getName(), dto.getType());
				entitySet.addAll(resourceEntity.getLatestFile());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entitySet;
    }
}
