package com.itextpdf.dito.manager.service.template.impl;


import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.filter.template.TemplateDependencyFilter;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateDependencyService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Component
public class TemplateDependencyServiceImpl extends AbstractController implements TemplateDependencyService {

    private final TemplateService templateService;

    private final TemplateRepository templateRepository;

    @Autowired
    public TemplateDependencyServiceImpl(TemplateService templateService, TemplateRepository templateRepository) {
        this.templateService = templateService;
        this.templateRepository = templateRepository;
    }

    @Override
    public Page<DependencyModel> list(final Pageable pageable, final String name, final TemplateDependencyFilter filter, final String search) {
        final String templateName = decodeBase64(name);
        final TemplateEntity templateEntity = templateService.get(templateName);
        final List<String> dependencyTypes = getDependencyAsString(filter.getDependencyType());
        final String directionType = getDirectionAsString(filter.getDirectionType());
        final String depend = getStringFromFilter(filter.getDependencyName());
        final Long version = getLongFromFilter(filter.getVersion());

        return StringUtils.isEmpty(search)
                ? templateRepository.filter(pageable, templateEntity.getId(), depend, version, directionType, dependencyTypes)
                : templateRepository.search(pageable, templateEntity.getId(), depend, version, directionType, dependencyTypes, search.toLowerCase());
    }

    @Override
    public List<DependencyModel> list(String name) {
        final String templateName = decodeBase64(name);
        final TemplateEntity templateEntity = templateService.get(templateName);
        return templateRepository.list(templateEntity.getId());


    }

    private List<String> getDependencyAsString(final List<DependencyType> list) {
        final List<String> listStrings = new ArrayList<>();
        if (list != null) {
            for (DependencyType dependencyType : list) {
                listStrings.add(dependencyType.toString());
            }
        }
        return listStrings;
    }

    private String getDirectionAsString(final List<DependencyDirectionType> list) {
        return list != null && list.size() == 1
                ? list.get(0).toString()
                : "";
    }

}
