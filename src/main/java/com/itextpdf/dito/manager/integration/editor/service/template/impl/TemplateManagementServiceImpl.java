package com.itextpdf.dito.manager.integration.editor.service.template.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.integration.InconsistencyException;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.sdk.core.dependency.api.TemplateDependency;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.DefaultTemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.TemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TemplateManagementServiceImpl implements TemplateManagementService {
    private static final String DEFAULT_COMMENT_FOR_NEW_VERSION = "Updated with iText DITO Editor";

    private final TemplateService templateService;
    private final TemplateAssetRetriever resourceAssetRetriever;
    private final TemplateAssetRetriever templateAssetRetriever;
    private final TemplateRepository templateRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceService resourceService;
    private final TemplateLoader templateLoader;

    public TemplateManagementServiceImpl(final TemplateService templateService,
                                         final TemplateAssetRetriever resourceAssetRetriever,
                                         final TemplateAssetRetriever templateAssetRetriever,
                                         final TemplateRepository templateRepository,
                                         final ResourceMapper resourceMapper,
                                         final ResourceService resourceService,
                                         final TemplateLoader templateLoader) {
        this.templateService = templateService;
        this.resourceAssetRetriever = resourceAssetRetriever;
        this.templateAssetRetriever = templateAssetRetriever;
        this.resourceMapper = resourceMapper;
        this.resourceService = resourceService;
        this.templateRepository = templateRepository;
        this.templateLoader = templateLoader;
    }

    @Override
    public TemplateEntity get(final String name) {
        return templateService.get(name);
    }

    @Override
    public List<TemplateEntity> getAll() {
        return templateService.getAll(List.of(TemplateTypeEnum.STANDARD, TemplateTypeEnum.HEADER, TemplateTypeEnum.FOOTER));
    }

    @Override
    @Transactional(rollbackOn = InconsistencyException.class)
    public TemplateEntity createNewVersion(final String name, final byte[] data, final String email,
                                           final String newName) {
        final TemplateEntity templateEntity = templateService.createNewVersion(name, data, email, DEFAULT_COMMENT_FOR_NEW_VERSION, newName, null);
        templateEntity.getLatestFile().setResourceFiles(provideConsistency(templateEntity.getLatestFile().getData()));
        provideFirstVersionTemplateCreation(templateEntity);
        return templateRepository.save(templateEntity);
    }

    @Override
    public TemplateEntity create(final String name, final String email) {
        return templateService.create(name, TemplateTypeEnum.STANDARD, null, email, null);
    }

    @Override
    public TemplateEntity create(final String name, final byte[] data, final String dataCollectionName, final String email) {
        final TemplateEntity templateEntity = templateService.create(name, TemplateTypeEnum.STANDARD, dataCollectionName, email, data, null);
        templateEntity.getLatestFile().setResourceFiles(provideConsistency(templateEntity.getLatestFile().getData()));
        return templateRepository.save(templateEntity);
    }

    @Override
    public TemplateEntity delete(final String templateName) {
        return templateService.delete(templateName);
    }

	private void provideFirstVersionTemplateCreation(final TemplateEntity templateEntity) {
		// versions sorted DESC in TemplateEntity
		if (templateEntity.getFiles().size() == 2
				&& Arrays.equals(templateEntity.getFiles().get(1).getData(), templateLoader.load())) {
			// updating empty version to editor version
			final TemplateFileEntity templateFileEditorVersion = templateEntity.getFiles().get(1);
			final TemplateFileEntity templateFileEmptyVersion = templateEntity.getFiles().get(0);
			templateFileEditorVersion.setData(templateFileEmptyVersion.getData());
			templateFileEditorVersion.setAuthor(templateFileEmptyVersion.getAuthor());
			templateFileEditorVersion.setComment(templateFileEmptyVersion.getComment());
			templateFileEditorVersion.setCreatedOn(templateFileEmptyVersion.getCreatedOn());
			templateFileEditorVersion.setDataCollectionFile(templateFileEmptyVersion.getDataCollectionFile());
			templateFileEditorVersion.setDeployed(templateFileEmptyVersion.getDeployed());
			templateFileEditorVersion.setModifiedOn(templateFileEmptyVersion.getModifiedOn());
			templateFileEditorVersion.setResourceFiles(templateFileEmptyVersion.getResourceFiles());
			//removing editor version
			templateEntity.getFiles().remove(0);
		}
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
                if (Objects.equals(ResourceTypeEnum.STYLESHEET, dto.getType())) {
                    final Set<ResourceFileEntity> styleSheetEntitySet = provideConsistency(resourceEntity.getLatestFile().get(0).getFile());
                    entitySet.addAll(styleSheetEntitySet);
                }
                entitySet.addAll(resourceEntity.getLatestFile());
            }
        } catch (IOException e) {
            throw new InconsistencyException();
        }
        return entitySet;
    }
}
