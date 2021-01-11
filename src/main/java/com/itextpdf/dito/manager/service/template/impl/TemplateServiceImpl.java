package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateLogEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.template.TemplateAlreadyExistsException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class TemplateServiceImpl extends AbstractService implements TemplateService {
    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final UserService userService;
    private final TemplateLoader templateLoader;
    private final DataCollectionRepository dataCollectionRepository;

    public TemplateServiceImpl(final TemplateFileRepository templateFileRepository,
                               final TemplateRepository templateRepository,
                               final UserService userService,
                               final TemplateLoader templateLoader,
                               final DataCollectionRepository dataCollectionRepository) {
        this.templateFileRepository = templateFileRepository;
        this.templateRepository = templateRepository;
        this.userService = userService;
        this.templateLoader = templateLoader;
        this.dataCollectionRepository = dataCollectionRepository;
    }

    @Override
    @Transactional
    public TemplateEntity create(final String templateName, final TemplateTypeEnum templateTypeEnum,
                                 final String dataCollectionName, final String email) {
        throwExceptionIfTemplateNameAlreadyIsRegistered(templateName);

        final TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setName(templateName);
        templateEntity.setType(templateTypeEnum);
        if (!StringUtils.isEmpty(dataCollectionName)) {
            templateEntity.setDataCollection(
                    dataCollectionRepository.findByName(dataCollectionName).orElseThrow(
                            () -> new DataCollectionNotFoundException(dataCollectionName)));
        }
        final UserEntity author = userService.findByEmail(email);

        final TemplateLogEntity logEntity = createLogEntity(templateEntity, author);


        final TemplateFileEntity templateFileEntity = new TemplateFileEntity();
        templateFileEntity.setAuthor(author);
        templateFileEntity.setVersion(1L);
        templateFileEntity.setData(templateLoader.load());
        templateFileEntity.setTemplate(templateEntity);
        templateFileEntity.setAuthor(author);
        templateFileEntity.setCreatedOn(new Date());
        templateFileEntity.setModifiedOn(new Date());

        templateEntity.setFiles(Collections.singletonList(templateFileEntity));
        templateEntity.setTemplateLogs(Collections.singletonList(logEntity));

        return templateRepository.save(templateEntity);
    }

    private TemplateLogEntity createLogEntity(final TemplateEntity templateEntity, final String email) {
        final UserEntity author = userService.findByEmail(email);

        return createLogEntity(templateEntity, author);
    }

    private TemplateLogEntity createLogEntity(final TemplateEntity templateEntity, final UserEntity author) {
        final TemplateLogEntity logEntity = new TemplateLogEntity();
        logEntity.setAuthor(author);
        logEntity.setDate(new Date());
        logEntity.setTemplate(templateEntity);
        return logEntity;
    }

    @Override
    public Page<TemplateEntity> getAll(Pageable pageable, TemplateFilter templateFilter, String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(templateFilter.getName());
        final String modifiedBy = getStringFromFilter(templateFilter.getModifiedBy());
        final List<TemplateTypeEnum> types = templateFilter.getType();
        final String dataCollectionName = getStringFromFilter(templateFilter.getDataCollection());

        Date editedOnStartDate = null;
        Date editedOnEndDate = null;
        final List<String> editedOnDateRange = templateFilter.getEditedOn();
        if (editedOnDateRange != null) {
            if (editedOnDateRange.size() != 2) {
                throw new InvalidDateRangeException();
            }
            editedOnStartDate = getStartDateFromRange(editedOnDateRange);
            editedOnEndDate = getEndDateFromRange(editedOnDateRange);
        }

        return StringUtils.isEmpty(searchParam)
                ? templateRepository
                .filter(pageWithSort, name, modifiedBy, types, dataCollectionName, editedOnStartDate, editedOnEndDate)
                : templateRepository
                .search(pageWithSort, name, modifiedBy, types, dataCollectionName, editedOnStartDate,
                        editedOnEndDate, searchParam.toLowerCase());
    }

    @Override
    public TemplateEntity get(final String name) {
        return findByName(name);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return TemplateRepository.SUPPORTED_SORT_FIELDS;
    }

    @Override
    public TemplateEntity update(final String name, final TemplateEntity updatedTemplateEntity, final String userEmail) {
        final TemplateEntity existingTemplate = findByName(name);
        if (!existingTemplate.getName().equals(updatedTemplateEntity.getName())) {
            existingTemplate.setName(updatedTemplateEntity.getName());
            throwExceptionIfTemplateNameAlreadyIsRegistered(updatedTemplateEntity.getName());
        }
        existingTemplate.setDescription(updatedTemplateEntity.getDescription());

        final TemplateLogEntity logEntity = createLogEntity(existingTemplate, userEmail);
        final Collection<TemplateLogEntity> templateLogs = existingTemplate.getTemplateLogs();
        templateLogs.add(logEntity);
        existingTemplate.setTemplateLogs(templateLogs);

        //TODO add logging version https://jira.itextsupport.com/browse/DTM-758
        return templateRepository.save(existingTemplate);
    }

    @Override
    public TemplateEntity createNewVersion(final String name, final byte[] data, final String email, final String comment) {
        final TemplateEntity existingTemplateEntity = findByName(name);
        final UserEntity userEntity = userService.findByEmail(email);

        final Long oldVersion = templateFileRepository.findFirstByTemplate_IdOrderByVersionDesc(existingTemplateEntity.getId()).getVersion();
        final TemplateLogEntity logEntity = createLogEntity(existingTemplateEntity, userEntity);

        if (data != null) {
            final TemplateFileEntity fileEntity = new TemplateFileEntity();
            fileEntity.setTemplate(existingTemplateEntity);
            fileEntity.setVersion(oldVersion + 1);
            fileEntity.setData(data);
            fileEntity.setComment(comment);
            fileEntity.setAuthor(userEntity);
            fileEntity.setCreatedOn(new Date());
            fileEntity.setModifiedOn(new Date());
            existingTemplateEntity.getFiles().add(fileEntity);
        }
        existingTemplateEntity.getTemplateLogs().add(logEntity);
        return templateRepository.save(existingTemplateEntity);
    }

    private void throwExceptionIfTemplateNameAlreadyIsRegistered(final String templateName) {
        if (templateRepository.findByName(templateName).isPresent()) {
            throw new TemplateAlreadyExistsException(templateName);
        }
    }

    private TemplateEntity findByName(final String name) {
        return templateRepository.findByName(name).orElseThrow(() -> new TemplateNotFoundException(name));
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("dataCollection")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "dataCollection.name");
                    }
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.author.firstName");
                    }
                    if (sortParam.getProperty().equals("editedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.date");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}
