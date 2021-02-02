package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDeploymentDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.stage.NoNextStageOnPromotionPathException;
import com.itextpdf.dito.manager.exception.template.TemplateDeploymentException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateVersionNotFoundException;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TemplateDeploymentServiceImpl implements TemplateDeploymentService {

    private static final Logger log = LogManager.getLogger(TemplateDeploymentServiceImpl.class);

    private static final String INSTANCE_DEPLOYMENT_ENDPOINT = "/api/deployments";

    private final WebClient webClient;
    private final TemplateMapper templateMapper;
    private final TemplateFileRepository templateFileRepository;
    private final TemplateProjectGenerator templateProjectGenerator;
    private final TemplateRepository templateRepository;

    public TemplateDeploymentServiceImpl(final TemplateMapper templateMapper,
            final TemplateFileRepository templateFileRepository,
            final TemplateProjectGenerator templateProjectGenerator,
            final TemplateRepository templateRepository) {
        this.templateMapper = templateMapper;
        this.templateFileRepository = templateFileRepository;
        this.templateProjectGenerator = templateProjectGenerator;
        this.templateRepository = templateRepository;
        webClient = WebClient.create();
    }

    @Override
    public void promoteOnDefaultStage(final String templateName) {
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        final TemplateFileEntity templateFileEntity = templateEntity.getLatestFile();

        for (final InstanceEntity instanceEntity : templateFileEntity.getInstance()) {
            promoteTemplateToInstance(instanceEntity, templateEntity);
        }
    }

    @Override
    public void promote(final String templateName, final Long version) {
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        final TemplateFileEntity templateFileEntity = getTemplateFileEntityByVersion(version, templateEntity);
        final StageEntity nextStage = getNextStage(templateFileEntity);
        for (final InstanceEntity instanceEntity : nextStage.getInstances()) {
            promoteTemplateToInstance(instanceEntity, templateEntity);
        }
        for (final InstanceEntity instanceEntity : templateFileEntity.getInstance()) {
            undeployTemplateFromInstance(instanceEntity.getRegisterToken(), instanceEntity.getSocket(), templateName);
        }
        templateFileEntity.setDeployed(true);
        templateFileEntity.setStage(nextStage);
        templateFileRepository.save(templateFileEntity);
    }

    @Override
    public void undeploy(final String templateName, final Long version) {
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        final TemplateFileEntity templateFileEntity = getTemplateFileEntityByVersion(version, templateEntity);
        final StageEntity defaultStageEntity = getDefaultStage(templateFileEntity);
        final StageEntity currentStageEntity = templateFileEntity.getStage();
        if (currentStageEntity.getSequenceOrder() == 0) {
            throw new TemplateDeploymentException("Template cannot be undeployed from DEV stage");
        }
        for (final InstanceEntity instance : currentStageEntity.getInstances()) {
            final String instanceSocket = instance.getSocket();
            final String instanceRegisterToken = instance.getRegisterToken();
            undeployTemplateFromInstance(instanceRegisterToken, instanceSocket, templateName);
        }
        for (final InstanceEntity instanceEntity : defaultStageEntity.getInstances()) {
            promoteTemplateToInstance(instanceEntity, templateEntity);
        }
        templateFileEntity.setDeployed(false);
        templateFileEntity.setStage(defaultStageEntity);
        templateFileRepository.save(templateFileEntity);
    }



    private void promoteTemplateToInstance(final InstanceEntity instanceEntity, final TemplateEntity templateEntity) {
        final String instanceSocket = instanceEntity.getSocket();
        final String instanceRegisterToken = instanceEntity.getRegisterToken();
        final TemplateDescriptorDTO templateDescriptorDTO = templateMapper.mapToDescriptor(templateEntity);
        final File templateProjectFile = templateProjectGenerator.generateZipByTemplateName(templateEntity);
        promoteTemplateToInstance(instanceRegisterToken, instanceSocket, templateDescriptorDTO, templateProjectFile);
    }

    private TemplateDeploymentDTO promoteTemplateToInstance(final String instanceRegisterToken,
            final String instanceSocket,
            final TemplateDescriptorDTO descriptorDTO,
            final File templateProject) {
        final String forceDeployParam = "?forceReplace=true";
        final String instanceDeploymentUrl = new StringBuilder().append(instanceSocket)
                .append(INSTANCE_DEPLOYMENT_ENDPOINT).append(forceDeployParam).toString();
        final Mono<TemplateDeploymentDTO> response = WebClient.create()
                .post()
                .uri(instanceDeploymentUrl)
                .headers(h -> h.setBearerAuth(instanceRegisterToken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(fromFile(descriptorDTO, templateProject)))
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(TemplateDeploymentDTO.class);
                });
        return response.block();
    }

    private TemplateDeploymentDTO undeployTemplateFromInstance(final String instanceRegisterToken,
            final String instanceSocket,
            final String templateAlias) {
        final String instanceDeploymentUrl = new StringBuilder().append(instanceSocket)
                .append(INSTANCE_DEPLOYMENT_ENDPOINT)
                .append("/")
                .append(templateAlias)
                .toString();
        final Mono<TemplateDeploymentDTO> response = WebClient.create()
                .delete()
                .uri(instanceDeploymentUrl)
                .headers(h -> h.setBearerAuth(instanceRegisterToken))
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(TemplateDeploymentDTO.class);
                });
        return response.block();
    }

    private MultiValueMap<String, HttpEntity<?>> fromFile(final TemplateDescriptorDTO templateDescriptorDTO,
            final File templateZipFile) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("template_project", new FileSystemResource(templateZipFile), MediaType.APPLICATION_OCTET_STREAM);
        builder.part("descriptor", templateDescriptorDTO, MediaType.APPLICATION_JSON);
        return builder.build();
    }

    private StageEntity getDefaultStage(final TemplateFileEntity templateFileEntity) {
        final PromotionPathEntity promotionPath = templateFileEntity.getStage().getPromotionPath();
        return promotionPath.getStages().get(0);
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
