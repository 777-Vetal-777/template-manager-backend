package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.TemplateTypeEntity;
import com.itextpdf.dito.manager.exception.template.TemplateTypeNotFoundException;
import com.itextpdf.dito.manager.repository.template.TemplateTypeRepository;
import com.itextpdf.dito.manager.service.template.TemplateTypeService;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TemplateTypeServiceImpl implements TemplateTypeService {
    private final TemplateTypeRepository templateTypeRepository;

    public TemplateTypeServiceImpl(TemplateTypeRepository templateTypeRepository) {
        this.templateTypeRepository = templateTypeRepository;
    }


    @Override
    public TemplateTypeEntity findTemplateType(String type) {
        Optional<TemplateTypeEntity> result = templateTypeRepository.findByName(type);

        if (result.isEmpty()) {
            throw new TemplateTypeNotFoundException(type);
        }

        return result.get();
    }

    @Override
    public List<TemplateTypeEntity> getAll() {
        return templateTypeRepository.findAll();
    }
}
