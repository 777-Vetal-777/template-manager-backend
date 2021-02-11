package com.itextpdf.dito.manager.integration.editor.controller.template.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateDescriptor;
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
    public TemplateDescriptor getDescriptor(final String templateId) {
        final String decodedTemplateId = decodeBase64(templateId);
        log.info("Request to get descriptor by template id {}.", decodedTemplateId);
        final TemplateEntity templateEntity = templateManagementService.get(decodedTemplateId);
        return templateDescriptorMapper.map(templateEntity);
    }

    @Override
    public byte[] get(final String templateId) {
        final String decodedTemplateId = decodeBase64(templateId);
        log.info("Request to get template file by template id {}.", decodedTemplateId);
        final TemplateEntity templateEntity = templateManagementService.get(decodedTemplateId);
        log.info("Response on get template file by template id {} successfully processed.", decodedTemplateId);
        return templateEntity.getLatestFile().getData();
    }

    @Override
    public List<TemplateDescriptor> getAllDescriptors(final String workspaceId) {
        log.info("Request to get all descriptors by workspace id {}.", workspaceId);
        // At now we support only single workspace, that's why all templates will be returned.
        return templateDescriptorMapper.map(templateManagementService.getAll());
    }

    @Override
    public TemplateDescriptor update(final Principal principal, final String templateId,
            final TemplateUpdateDescriptor descriptor,
            final byte[] data) {
        final String email = principal.getName();
        final String id = decodeBase64(templateId);
        log.info("Request to create new version of template with id {} received.", id);
        final String newName = descriptor != null ? descriptor.getName() : null;

        final TemplateEntity templateEntity = templateManagementService.createNewVersion(id, data, email, newName);
        log.info("Response to create new version of template with id {} processed.",id);
        return templateDescriptorMapper.map(templateEntity);
    }

    @Override
    public TemplateDescriptor add(final Principal principal, final String workspaceId,
            @Valid final TemplateAddDescriptor descriptor,
            final byte[] data) {
        log.info("Request to create new template with name {} received.", descriptor.getName());
        final String email = principal.getName();
        final TemplateEntity templateEntity = templateManagementService.create(descriptor.getName(), email);
        log.info("Response to create new template received with name {} processed. Created template id {}", descriptor.getName(), templateEntity.getId());
        return templateDescriptorMapper.map(templateEntity);
    }

    @Override
    public TemplateDescriptor delete(final String templateId) {
        final String decodedTemplateId = decodeBase64(templateId);
        log.info("Request to delete template with id {} received.",decodedTemplateId);
        final TemplateEntity templateEntity = templateManagementService.delete(decodedTemplateId);
        log.info("Responce to delete template with id {} successfully processed.",decodedTemplateId);
        return templateDescriptorMapper.map(templateEntity);
    }
}
