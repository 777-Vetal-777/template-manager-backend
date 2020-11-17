package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.UnsupportedTemplateTypeException;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.template.TemplateTypeRepository;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.security.Principal;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateTypeRepository templateTypeRepository;
    private final TemplateFileRepository templateFileRepository;
    private final UserService userService;

    public TemplateServiceImpl(final TemplateRepository templateRepository,
            final TemplateTypeRepository templateTypeRepository,
            final TemplateFileRepository templateFileRepository,
            final UserService userService) {
        this.templateRepository = templateRepository;
        this.templateTypeRepository = templateTypeRepository;
        this.templateFileRepository = templateFileRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void create(final TemplateCreateRequestDTO templateCreateRequestDTO, final Principal principal) {
        String type = templateCreateRequestDTO.getType();
        TemplateTypeEntity templateTypeEntity = processTemplateType(type);

        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setName(templateCreateRequestDTO.getName());
        templateEntity.setType(templateTypeEntity);
        TemplateEntity persistedTemplateEntity = templateRepository.save(templateEntity);

        TemplateFileEntity templateFileEntity = new TemplateFileEntity();
        templateFileEntity.setData(templateCreateRequestDTO.getData());
        templateFileEntity.setTemplate(persistedTemplateEntity);
        templateFileEntity.setAuthor(retrieveAuthor(principal.getName()));
        templateFileRepository.save(templateFileEntity);
    }

    private TemplateTypeEntity processTemplateType(final String type) {
        TemplateTypeEntity result;

        Optional<TemplateTypeEntity> optionalTemplateTypeEntity = templateTypeRepository.findByName(type);
        result = optionalTemplateTypeEntity.orElseThrow(UnsupportedTemplateTypeException::new);

        return result;
    }

    private UserEntity retrieveAuthor(final String email) {
        return userService.findByEmail(email);
    }
}
