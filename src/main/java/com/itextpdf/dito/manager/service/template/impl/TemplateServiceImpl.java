package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateAlreadyExistsException;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.template.TemplateTypeService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.itextpdf.dito.manager.filter.FilterUtils.getDateRangeFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class TemplateServiceImpl extends AbstractService implements TemplateService {
    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final TemplateTypeService templateTypeService;
    private final UserService userService;
    private final TemplateLoader templateLoader;
    private final DataCollectionRepository dataCollectionRepository;

    public TemplateServiceImpl(final TemplateFileRepository templateFileRepository,
                               final TemplateRepository templateRepository,
                               final TemplateTypeService templateTypeService,
                               final UserService userService,
                               final TemplateLoader templateLoader,
                               final DataCollectionRepository dataCollectionRepository) {
        this.templateFileRepository = templateFileRepository;
        this.templateRepository = templateRepository;
        this.templateTypeService = templateTypeService;
        this.userService = userService;
        this.templateLoader = templateLoader;
        this.dataCollectionRepository = dataCollectionRepository;
    }


    @Override
    @Transactional
    public TemplateEntity create(final String templateName, final String templateTypeName,
                                 final String dataCollectionName, final String email) {
        throwExceptionIfTemplateNameAlreadyIsRegistered(templateName);

        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setName(templateName);
        templateEntity.setType(templateTypeService.findTemplateType(templateTypeName));
        if (!StringUtils.isEmpty(dataCollectionName)) {
            templateEntity.setDataCollection(
                    dataCollectionRepository.findByName(dataCollectionName).orElseThrow(
                            () -> new DataCollectionNotFoundException(dataCollectionName)));
        }
        final TemplateEntity persistedTemplateEntity = templateRepository.save(templateEntity);

        TemplateFileEntity templateFileEntity = new TemplateFileEntity();
        templateFileEntity.setAuthor(userService.findByEmail(email));
        templateFileEntity.setData(templateLoader.load());
        templateFileEntity.setTemplate(persistedTemplateEntity);
        templateFileRepository.save(templateFileEntity);

        return persistedTemplateEntity;
    }

    @Override
    public Page<TemplateEntity> getAll(Pageable pageable, TemplateFilter templateFilter, String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(templateFilter.getName());
        final String modifiedBy = getStringFromFilter(templateFilter.getModifiedBy());
        final List<String> types = templateFilter.getType();
        final String dataCollectionName = getStringFromFilter(templateFilter.getDataCollection());
        final Date startDate =  getDateRangeFromFilter(templateFilter.getEditedOn()).get(0);
        final Date endDate =  getDateRangeFromFilter(templateFilter.getEditedOn()).get(1);
        return StringUtils.isEmpty(searchParam)
                ? templateRepository.filter(pageWithSort, name, modifiedBy, types, dataCollectionName, startDate, endDate)
                : templateRepository.search(pageWithSort, name, modifiedBy, types, dataCollectionName, startDate, endDate, searchParam.toLowerCase());
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return TemplateRepository.SUPPORTED_SORT_FIELDS;
    }

    private void throwExceptionIfTemplateNameAlreadyIsRegistered(final String templateName) {
        if (templateRepository.findByName(templateName).isPresent()) {
            throw new TemplateAlreadyExistsException(templateName);
        }
    }

    private Pageable updateSort(Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("type")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "type.name");
                    }
                    if (sortParam.getProperty().equals("dataCollection")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "dataCollection.name");
                    }
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.author.firstName");
                    }
                    if (sortParam.getProperty().equals("editedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.version");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}
