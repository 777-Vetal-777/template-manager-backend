package com.itextpdf.dito.manager.integration.editor.service.template.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.integration.InconsistencyException;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;
import com.itextpdf.dito.sdk.core.dependency.api.TemplateDependency;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.DefaultTemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.dependency.retriever.template.TemplateDependenciesRetriever;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TemplateManagementServiceImpl implements TemplateManagementService {

    private final TemplateService templateService;
    private final TemplateAssetRetriever resourceAssetRetriever;
    private final TemplateAssetRetriever templateAssetRetriever;
    private final TemplateRepository templateRepository;
    private final TemplateFileRepository templateFileRepository;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;
    private final TemplateDeploymentService templateDeploymentService;
    private final ResourceService resourceService;
    private final TemplateLoader templateLoader;
    private final UserService userService;
    private final Encoder encoder;

    public TemplateManagementServiceImpl(final TemplateService templateService,
                                         final TemplateAssetRetriever resourceAssetRetriever,
                                         final TemplateAssetRetriever templateAssetRetriever,
                                         final TemplateRepository templateRepository,
                                         final TemplateFileRepository templateFileRepository,
                                         final ResourceService resourceService,
                                         final TemplateLoader templateLoader,
                                         final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper,
                                         final TemplateDeploymentService templateDeploymentService,
                                         final Encoder encoder,
                                         final UserService userService) {
        this.templateService = templateService;
        this.resourceAssetRetriever = resourceAssetRetriever;
        this.templateAssetRetriever = templateAssetRetriever;
        this.templateFileRepository = templateFileRepository;
        this.resourceService = resourceService;
        this.templateRepository = templateRepository;
        this.templateLoader = templateLoader;
        this.encoder = encoder;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
        this.userService = userService;
        this.templateDeploymentService = templateDeploymentService;
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
                                           final String newName, final String comment) {
        final TemplateEntity existingTemplate = templateService.get(name);
        final TemplateEntity templateEntity;
        final TemplateFileEntity firstFileOfTemplate = templateFileRepository.findFirstByTemplate_IdOrderByVersionDesc(existingTemplate.getId());
        if (firstFileOfTemplate.getVersion() == 1 && Arrays.equals(firstFileOfTemplate.getData(), templateLoader.load())){
            templateEntity = provideFirstVersionTemplateCreation(existingTemplate, data, comment, email);
            templateDeploymentService.promoteOnDefaultStage(templateEntity.getLatestFile());
        } else {
            templateEntity = templateService.createNewVersion(name, data, email, comment, newName, null);
        }
        templateEntity.getLatestFile().setResourceFiles(provideConsistency(templateEntity.getLatestFile().getData()));
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

	private TemplateEntity provideFirstVersionTemplateCreation(final TemplateEntity templateEntity, final byte[] data, final String comment, final String userEmail) {
        final UserEntity creatorEntity = userService.findActiveUserByEmail(userEmail);
        final TemplateFileEntity templateFileEntity = templateFileRepository.findFirstByTemplate_IdOrderByVersionDesc(templateEntity.getId());
        templateFileEntity.setData(data);
        templateFileEntity.setComment(comment);
        templateFileEntity.setCreatedOn(new Date());
        templateFileEntity.setModifiedOn(new Date());
        templateFileEntity.setAuthor(creatorEntity);
        templateEntity.setLatestFile(templateFileEntity);
        return templateEntity;
    }

    private Set<ResourceFileEntity> provideConsistency(final byte[] data) {
        final TemplateDependenciesRetriever retriever = new DefaultTemplateDependenciesRetriever(templateAssetRetriever,
                resourceAssetRetriever);
        final Set<ResourceFileEntity> entitySet = new HashSet<>();
        try {
            final List<TemplateDependency> dependencies = retriever.getDependencies(new ByteArrayInputStream(data));
            for (final TemplateDependency td : dependencies) {
                final String decodedUrl = encoder.decode(td.getUri().toString().replace("dito-asset://", ""));
                final ResourceIdDTO dto = resourceLeafDescriptorMapper.deserialize(decodedUrl);
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
