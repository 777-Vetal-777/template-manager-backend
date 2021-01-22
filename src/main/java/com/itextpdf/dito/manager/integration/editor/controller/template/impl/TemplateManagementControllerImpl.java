package com.itextpdf.dito.manager.integration.editor.controller.template.impl;

//import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
//import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateDescriptor;
//import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateUpdateDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateDescriptorMapper;
import com.itextpdf.dito.manager.service.template.TemplateService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateManagementControllerImpl extends AbstractController implements TemplateManagementController {
    private final TemplateService templateService;
    private final TemplateDescriptorMapper templateDescriptorMapper;

    public TemplateManagementControllerImpl(final TemplateService templateService,
            final TemplateDescriptorMapper templateDescriptorMapper) {
        this.templateService = templateService;
        this.templateDescriptorMapper = templateDescriptorMapper;
    }

//    @Override
//    public TemplateDescriptor getDescriptor(final String templateId) {
//        final String decodedTemplateId = decodeBase64(templateId);
//        final TemplateEntity templateEntity = templateService.get(decodedTemplateId);
//        return templateDescriptorMapper.map(templateEntity);
//    }
//
//    @Override
//    public InputStream get(final String templateId) {
//        final String decodedTemplateId = decodeBase64(templateId);
//        final TemplateEntity templateEntity = templateService.get(decodedTemplateId);
//        return new ByteArrayInputStream(templateEntity.getLatestFile().getData());
//    }
//
//    @Override
//    public List<TemplateDescriptor> getAllDescriptors(final String workspaceId) {
//        // At now we support only single workspace, that's why all templates will be returned.
//        return templateDescriptorMapper.map(templateService.getAll());
//    }
//
//    @Override
//    public TemplateDescriptor update(final Principal principal, final String templateId,
//            final TemplateUpdateDescriptor descriptor,
//            final InputStream data) throws IOException {
//        final String email = principal.getName();
//        final String decodedTemplateId = decodeBase64(templateId);
//        final String newName = descriptor.getName();
//        final byte[] dataAsByteArray = new byte[data.available()];
//        data.read(dataAsByteArray);
//
//        final TemplateEntity templateEntity = templateService
//                .createNewVersion(decodedTemplateId, dataAsByteArray, email, null, newName);
//
//        return templateDescriptorMapper.map(templateEntity);
//    }
//
//    @Override
//    public TemplateDescriptor add(final Principal principal, final String workspaceId,
//            @Valid final TemplateAddDescriptor descriptor,
//            final InputStream data) {
//        final String email = principal.getName();
//        final TemplateEntity templateEntity = templateService
//                .create(descriptor.getName(), TemplateTypeEnum.STANDARD, null, email);
//        return templateDescriptorMapper.map(templateEntity);
//    }
//
//    @Override
//    public TemplateDescriptor delete(final String templateId) {
//        final String decodedTemplateId = decodeBase64(templateId);
//
//        final TemplateEntity templateEntity = templateService.delete(decodedTemplateId);
//
//        return templateDescriptorMapper.map(templateEntity);
//    }
}
