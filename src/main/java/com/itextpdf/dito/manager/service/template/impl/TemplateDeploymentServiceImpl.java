package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDeploymentDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.instance.DefaultInstanceException;
import com.itextpdf.dito.manager.exception.stage.NoNextStageOnPromotionPathException;
import com.itextpdf.dito.manager.exception.template.TemplateDeploymentException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateVersionNotFoundException;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceHasNoDevelopmentStageException;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.itextpdf.dito.manager.util.FilesUtils;
import com.itextpdf.dito.manager.util.TemplateDeploymentUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class TemplateDeploymentServiceImpl implements TemplateDeploymentService {
    private static final Logger log = LogManager.getLogger(TemplateDeploymentServiceImpl.class);
    private final TemplateMapper templateMapper;
    private final TemplateFileRepository templateFileRepository;
    private final TemplateProjectGenerator templateProjectGenerator;
    private final TemplateRepository templateRepository;
    private final StageRepository stageRepository;
    private final InstanceClient instanceClient;

    public TemplateDeploymentServiceImpl(final TemplateMapper templateMapper,
                                         final TemplateFileRepository templateFileRepository,
                                         final TemplateProjectGenerator templateProjectGenerator,
                                         final TemplateRepository templateRepository,
                                         final StageRepository stageRepository,
                                         final InstanceClient instanceClient) {
        this.templateMapper = templateMapper;
        this.templateFileRepository = templateFileRepository;
        this.templateProjectGenerator = templateProjectGenerator;
        this.templateRepository = templateRepository;
        this.stageRepository = stageRepository;
        this.instanceClient = instanceClient;
    }

    /**
     * Promote template on default DEV-stage.
     * When template is promoted on default stage it should have alias <template_name>_version-<version-number> in order to allow multiple versions of template on DEV-stage.
     *
     * @param templateFileEntity to be promoted
     */
    @Override
    public void promoteOnDefaultStage(final TemplateFileEntity templateFileEntity) {
        log.info("Promote on default stage template: {} was started ", templateFileEntity);
        final boolean isDefaultStage = true;
        for (final InstanceEntity instanceEntity : templateFileEntity.getInstance()) {
            promoteTemplateToInstance(instanceEntity, templateFileEntity, isDefaultStage);
        }
        log.info("Promote on default stage template: {} was finished successfully ", templateFileEntity);
    }

    /**
     * Promote template to all instances of next stage.
     * When template is promoted on all stages beside DEV-stage it has alias which is equal to template name.
     *
     * @param templateName template name to be promoted
     * @param version      version of template to be promoted
     */
    @Override
    public TemplateFileEntity promote(final String templateName, final Long version) {
        log.info("Promote template version to next stage by name: {} and version: {} was started", templateName, version);
        final boolean isDefaultStage = false;
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        final TemplateFileEntity templateFileEntity = getTemplateFileEntityByVersion(version, templateEntity);
        final StageEntity nextStage = getNextStage(templateFileEntity);
        for (final InstanceEntity instanceEntity : nextStage.getInstances()) {
            promoteTemplateToInstance(instanceEntity, templateFileEntity, isDefaultStage);
        }
        for (final InstanceEntity instanceEntity : templateFileEntity.getInstance()) {
            removeTemplateFromInstance(instanceEntity.getRegisterToken(), instanceEntity.getSocket(), templateFileEntity);
        }
        final Optional<TemplateFileEntity> previousStageTemplateVersion = templateFileRepository.findByStageAndTemplate(nextStage, templateEntity);
        if (previousStageTemplateVersion.isPresent()) {
            final TemplateFileEntity previouslyDeployedTemplateVersion = previousStageTemplateVersion.get();
            previouslyDeployedTemplateVersion.setStage(getDefaultStage());
            previouslyDeployedTemplateVersion.setDeployed(false);
            templateFileRepository.save(previouslyDeployedTemplateVersion);
        }
        templateFileEntity.setDeployed(true);
        templateFileEntity.setStage(nextStage);
        final TemplateFileEntity savedTemplateFileEntity = templateFileRepository.save(templateFileEntity);
        log.info("Promote template version to next stage by name: {} and version: {} was finished successfully", templateName, version);
        return savedTemplateFileEntity;
    }

    /**
     * Un-deploy template from stage back to default DEV-stage
     *
     * @param templateName template name to be un-deployed.
     * @param version      template version to be un-deployed.
     */
    @Override
    public TemplateFileEntity undeploy(final String templateName, final Long version) {
        log.info("Undeploy template version by name: {} and version: {} was started", templateName, version);
        final boolean isDefaultStage = true;
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        final TemplateFileEntity templateFileEntity = getTemplateFileEntityByVersion(version, templateEntity);
        final StageEntity defaultStageEntity = getDefaultStage();
        final StageEntity currentStageEntity = templateFileEntity.getStage();
        if (currentStageEntity.getSequenceOrder() == 0) {
            throw new TemplateDeploymentException("Template cannot be undeployed from DEV stage");
        }
        for (final InstanceEntity instance : currentStageEntity.getInstances()) {
            final String instanceSocket = instance.getSocket();
            final String instanceRegisterToken = instance.getRegisterToken();
            removeTemplateFromInstance(instanceRegisterToken, instanceSocket, templateFileEntity);
        }
        for (final InstanceEntity instanceEntity : defaultStageEntity.getInstances()) {
            promoteTemplateToInstance(instanceEntity, templateFileEntity, isDefaultStage);
        }
        templateFileEntity.setDeployed(false);
        templateFileEntity.setStage(defaultStageEntity);
        final TemplateFileEntity savedTemplateFileEntity = templateFileRepository.save(templateFileEntity);
        log.info("Undeploy template version by name: {} and version: {} was finished successfully", templateName, version);
        return savedTemplateFileEntity;
    }

    /**
     * This method should be used only when templated is deleted.
     *
     * @param templateVersions to be deleted from system.
     */
    @Override
    public void removeAllVersionsFromDefaultStage(final List<TemplateFileEntity> templateVersions) {
        log.info("Remove all versions from default stage: {} was started", templateVersions);
        final InstanceEntity defaultInstance = getDefaultInstance();
        templateVersions.forEach(templateVersion -> removeTemplateFromInstance(defaultInstance.getRegisterToken(), defaultInstance.getSocket(), templateVersion));
        log.info("Remove all versions from default stage: {} was finished successfully", templateVersions);
    }

    @Override
    public StageEntity getNextStage(final String templateName, final Long version) {
        log.info("Get next stage by templateName: {} and version: {} was started", templateName, version);
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        final TemplateFileEntity templateFileEntity = getTemplateFileEntityByVersion(version, templateEntity);
        final StageEntity stageEntity = getNextStage(templateFileEntity);
        log.info("Get next stage by templateName: {} and version: {} was finished successfully", templateName, version);
        return stageEntity;
    }

    private void promoteTemplateToInstance(final InstanceEntity instanceEntity, final TemplateFileEntity templateFileEntity, final boolean isDefaultInstance) {
        final TemplateEntity templateEntity = templateFileEntity.getTemplate();
        final String instanceSocket = instanceEntity.getSocket();
        final String instanceRegisterToken = instanceEntity.getRegisterToken();
        final TemplateDescriptorDTO templateDescriptorDTO = templateMapper.mapToDescriptor(templateFileEntity, isDefaultInstance);
        final File templateProjectFile = templateProjectGenerator.generateZippedProjectByTemplate(templateEntity, (DataSampleFileEntity) null);
        promoteTemplateToInstance(instanceRegisterToken, instanceSocket, templateDescriptorDTO, templateProjectFile);
        FileUtils.deleteQuietly(templateProjectFile);
    }

    private TemplateDeploymentDTO promoteTemplateToInstance(final String instanceRegisterToken,
                                                            final String instanceSocket,
                                                            final TemplateDescriptorDTO descriptorDTO,
                                                            final File templateProject) {
        return instanceClient.promoteTemplateToInstance(instanceRegisterToken, instanceSocket, descriptorDTO, templateProject);
    }

    private TemplateDeploymentDTO removeTemplateFromInstance(final String instanceRegisterToken,
                                                             final String instanceSocket,
                                                             final TemplateFileEntity templateFileEntity) {
        final String templateAlias = isOnDefaultStage(templateFileEntity)
                ? TemplateDeploymentUtils.getTemplateAliasForDefaultInstance(templateFileEntity)
                : templateFileEntity.getTemplate().getName();
        return instanceClient.removeTemplateFromInstance(instanceRegisterToken, instanceSocket, templateAlias);
    }

    private boolean isOnDefaultStage(final TemplateFileEntity templateFileEntity) {
        return templateFileEntity.getStage() == getDefaultStage();
    }

    private StageEntity getDefaultStage() {
        return stageRepository.findDefaultStage().orElseThrow(WorkspaceHasNoDevelopmentStageException::new);
    }

    private InstanceEntity getDefaultInstance() {
        final StageEntity defaultStage = getDefaultStage();
        final List<InstanceEntity> instances = defaultStage.getInstances();
        if (CollectionUtils.isEmpty(instances) || instances.size() > 1) {
            throw new DefaultInstanceException();
        }
        return instances.get(0);
    }

    private StageEntity getNextStage(final TemplateFileEntity templateFileEntity) {
        final int nextStageOrder = templateFileEntity.getStage().getSequenceOrder() + 1;
        final PromotionPathEntity promotionPath = templateFileEntity.getStage().getPromotionPath();
        final List<StageEntity> promotionPathStages = promotionPath.getStages();
        if (nextStageOrder > promotionPathStages.size() - 1) {
            throw new NoNextStageOnPromotionPathException();
        }
        return promotionPathStages.get(nextStageOrder);
    }

    private TemplateEntity getTemplateByName(final String templateName) {
        return templateRepository.findByName(templateName)
                .orElseThrow(() -> new TemplateNotFoundException(templateName));
    }

    private TemplateFileEntity getTemplateFileEntityByVersion(final Long version, final TemplateEntity templateEntity) {
        return templateFileRepository.findByVersionAndTemplate(version, templateEntity)
                .orElseThrow(() -> new TemplateVersionNotFoundException(String.valueOf(version)));
    }
}
