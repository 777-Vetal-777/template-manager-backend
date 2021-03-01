package com.itextpdf.dito.manager.service.template.impl;


import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateDependencyService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getListStringsFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class TemplateDependencyServiceImpl extends AbstractController implements TemplateDependencyService {
    private static final Logger log = LogManager.getLogger(TemplateDependencyServiceImpl.class);
    private final TemplateService templateService;
    private final TemplateRepository templateRepository;

    public TemplateDependencyServiceImpl(final TemplateService templateService, final TemplateRepository templateRepository) {
        this.templateService = templateService;
        this.templateRepository = templateRepository;
    }

    @Override
    public Page<DependencyModel> list(final Pageable pageable, final String name, final DependencyFilter filter, final String search) {
        log.info("Get list template dependencies by name: {} and filter: {} and search: {} was started", name, filter, search);
        final String templateName = decodeBase64(name);
        final TemplateEntity templateEntity = templateService.get(templateName);
        final List<String> dependencyTypes = getDependencyAsString(filter.getDependencyType());
        final String directionType = getDirectionAsString(filter.getDirectionType());
        final String depend = getStringFromFilter(filter.getName());
        final List<String> stages = getListStringsFromFilter(filter.getStage());
        final Long version = getLongFromFilter(filter.getVersion());
        final Pageable pageWithSort = updateSort(pageable);

        final Page<DependencyModel> dependencyModels = StringUtils.isEmpty(search)
                ? templateRepository.filter(pageWithSort, templateEntity.getId(), depend, version, directionType, dependencyTypes, stages)
                : templateRepository.search(pageWithSort, templateEntity.getId(), depend, version, directionType, dependencyTypes, stages, search.toLowerCase());
        log.info("Get list template dependencies by name: {} and filter: {} and search: {} was finished successfully", name, filter, search);
        return dependencyModels;
    }

    @Override
    public List<DependencyModel> list(final String name) {
        log.info("Get list template dependencies by name: {} was started", name);
        final String templateName = decodeBase64(name);
        final TemplateEntity templateEntity = templateService.get(templateName);
        final List<DependencyModel> dependencyModels = templateRepository.getTemplateHardRelations(templateEntity.getId());
        log.info("Get list template dependencies by name: {} was finished successfully", name);
        return dependencyModels;
    }

    private List<String> getDependencyAsString(final List<DependencyType> list) {
        final List<String> listStrings = new ArrayList<>();
        if (list != null) {
            for (final DependencyType dependencyType : list) {
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

    private Pageable updateSort(final Pageable pageable) {
        final Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("name")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "lower_name");
                    }
                    if (sortParam.getProperty().equals("stage")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "lower_stage");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

}
