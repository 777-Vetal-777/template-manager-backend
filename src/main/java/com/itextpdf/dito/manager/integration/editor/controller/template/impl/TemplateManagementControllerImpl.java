package com.itextpdf.dito.manager.integration.editor.controller.template.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.ExternalTemplateDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateCommitDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateUpdateDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class TemplateManagementControllerImpl extends AbstractController implements TemplateManagementController {
    private static final Logger log = LogManager.getLogger(TemplateManagementControllerImpl.class);
    private final TemplateManagementService templateManagementService;
    private final TemplateDescriptorMapper templateDescriptorMapper;

    public TemplateManagementControllerImpl(final TemplateManagementService templateManagementService,
                                            final TemplateDescriptorMapper templateDescriptorMapper) {
        this.templateManagementService = templateManagementService;
        this.templateDescriptorMapper = templateDescriptorMapper;
    }

    @Override
    public ExternalTemplateDescriptor getDescriptor(final String templateId) {
        log.info("Request to get descriptor by template id {}.", templateId);
        final TemplateEntity templateEntity = templateManagementService.get(templateId);
        return templateDescriptorMapper.map(templateEntity);
    }

    @Override
    public byte[] get(final String templateId) {
        log.info("Request to get template file by template id {}.", templateId);
        final TemplateEntity templateEntity = templateManagementService.get(templateId);
        log.info("Response on get template file by template id {} successfully processed.", templateId);
        return templateEntity.getLatestFile().getData();
    }

    @Override
    public List<ExternalTemplateDescriptor> getAllDescriptors(final String workspaceId) {
        log.info("Request to get all descriptors by workspace id {}.", workspaceId);
        // At now we support only single workspace, that's why all templates will be returned.
        return templateDescriptorMapper.map(templateManagementService.getAll());
    }

    @Override
    public ExternalTemplateDescriptor update(final Principal principal, final String templateId,
                                             final TemplateUpdateDescriptor descriptor,
                                             final TemplateCommitDescriptor commit,
                                             final byte[] data) {
        final String email = principal.getName();
        log.info("Request to create new version of template with id {} received.", templateId);
        final TemplateEntity currentTemplateEntity = templateManagementService.get(templateId);
        final String newName = descriptor != null ? descriptor.getName() : null;
        final String commitMessage = commit != null ? commit.getMessage() : null;
        final TemplateEntity templateEntity = templateManagementService.createNewVersion(currentTemplateEntity.getName(), data, email, newName, commitMessage);
        log.info("Response to create new version of template with id {} processed.", templateId);
        return templateDescriptorMapper.map(templateEntity);
    }

    @Override
    public ExternalTemplateDescriptor add(final Principal principal, final String workspaceId,
                                          @Valid final TemplateAddDescriptor descriptor,
                                          final byte[] data) {
        log.info("Request to create new template with name {} received.", descriptor.getName());
        final String email = principal.getName();
        final TemplateEntity templateEntity = templateManagementService.create(descriptor.getName(), data, null, email);
        log.info("Response to create new template received with name {} processed. Created template id {}", descriptor.getName(), templateEntity.getId());
        return templateDescriptorMapper.map(templateEntity);
    }

    @Override
    public ExternalTemplateDescriptor delete(final String templateId) {
        log.info("Request to delete template with id {} received.", templateId);
        final TemplateEntity templateEntity = templateManagementService.delete(templateId);
        log.info("Responce to delete template with id {} successfully processed.", templateId);
        return templateDescriptorMapper.map(templateEntity);
    }
}
