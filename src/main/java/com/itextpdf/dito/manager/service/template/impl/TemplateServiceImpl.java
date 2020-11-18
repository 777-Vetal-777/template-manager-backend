package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.TemplateNameAlreadeIsRegisteredException;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.template.TemplateTypeService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {
    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final TemplateTypeService templateTypeService;
    private final UserService userService;
    private final TemplateLoader templateLoader;

    public TemplateServiceImpl(final TemplateFileRepository templateFileRepository,
            final TemplateRepository templateRepository,
            final TemplateTypeService templateTypeService,
            final UserService userService,
            final TemplateLoader templateLoader) {
        this.templateFileRepository = templateFileRepository;
        this.templateRepository = templateRepository;
        this.templateTypeService = templateTypeService;
        this.userService = userService;
        this.templateLoader = templateLoader;
    }


    @Override
    @Transactional
    public TemplateFileEntity create(final TemplateCreateRequestDTO templateCreateRequestDTO, String email) {
        throwExceptionIfTemplateNameAlreadyIsRegistered(templateCreateRequestDTO.getName());

        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setName(templateCreateRequestDTO.getName());
        templateEntity.setType(templateTypeService.findTemplateType(templateCreateRequestDTO.getType()));
        templateRepository.save(templateEntity);

        TemplateFileEntity templateFileEntity = new TemplateFileEntity();
        templateFileEntity.setAuthor(userService.findByEmail(email));
        templateFileEntity.setData(templateLoader.load());
        templateFileEntity.setTemplate(templateEntity);

        return templateFileRepository.save(templateFileEntity);
    }

    @Override
    public Page<TemplateEntity> getAll(Pageable pageable) {
        return templateRepository.findAll(pageable);
    }

    private void throwExceptionIfTemplateNameAlreadyIsRegistered(final String templateName) {
        if (templateRepository.findByName(templateName).isPresent()) {
            throw new TemplateNameAlreadeIsRegisteredException(templateName);
        }
    }
}
