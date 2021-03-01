package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.template.TemplateVersionNotFoundException;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.template.TemplateVersionsService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class TemplateVersionsServiceImpl extends AbstractService implements TemplateVersionsService {
    private static final Logger log = LogManager.getLogger(TemplateVersionsServiceImpl.class);
    private final TemplateService templateService;
    private final TemplateFileRepository templateFileRepository;
    private final UserService userService;

    public TemplateVersionsServiceImpl(final TemplateService templateService,
                                       final TemplateFileRepository templateFileRepository,
                                       final UserService userService) {
        this.templateService = templateService;
        this.templateFileRepository = templateFileRepository;
        this.userService = userService;
    }

    @Override
    public Page<FileVersionModel> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam) {
        log.info("Get template versions by name: {} and filter: {} and searchParam: {} was started", name, filter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final TemplateEntity template = templateService.get(name);

        final Pageable pageWithSort = updateSort(pageable);
        final Long version = filter.getVersion();
        final String modifiedBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());
        final String stageName = getStringFromFilter(filter.getStage());

        Date modifiedOnStartDate = null;
        Date modifiedOnEndDate = null;
        final List<String> modifiedOnDateRange = filter.getModifiedOn();
        if (modifiedOnDateRange != null) {
            if (modifiedOnDateRange.size() != 2) {
                throw new IllegalArgumentException("Date range should contain two elements: start date and end date");
            }
            modifiedOnStartDate = getStartDateFromRange(modifiedOnDateRange);
            modifiedOnEndDate = getEndDateFromRange(modifiedOnDateRange);
        }
        final Page<FileVersionModel> versionModels = StringUtils.isEmpty(searchParam)
                ? templateFileRepository.filter(pageWithSort, template.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, stageName)
                : templateFileRepository.search(pageWithSort, template.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, stageName, searchParam.toLowerCase());
        log.info("Get template versions by name: {} and filter: {} and searchParam: {} was finished successfully", name, filter, searchParam);

        return versionModels;
    }

    @Override
    public TemplateEntity rollbackVersion(final String templateName, final Long version, final String userEmail) {
        log.info("Rollback vesion by templateName: {} and version: {} and userEmail: {} was started", templateName, version, userEmail);
        final UserEntity currentUser = userService.findActiveUserByEmail(userEmail);
        final TemplateEntity templateEntity = templateService.get(templateName);
        final TemplateFileEntity templateFileEntityToBeRevertedTo = templateFileRepository.findByVersionAndTemplate(version, templateEntity)
                .orElseThrow(() -> new TemplateVersionNotFoundException(String.valueOf(version)));
        final TemplateEntity updatedTemplateEntity = templateService.rollbackTemplate(templateEntity, templateFileEntityToBeRevertedTo, currentUser);
        log.info("Rollback vesion by templateName: {} and version: {} and userEmail: {} was finished successfully", templateName, version, userEmail);
        return updatedTemplateEntity;
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort;
        if (pageable.getSort().isSorted()) {
            newSort = Sort.by(pageable.getSort().stream()
                    .map(sortParam -> {
                        if (sortParam.getProperty().equals("modifiedBy")) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "author.firstName");
                        }
                        if (sortParam.getProperty().equals("stage")) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "stage.name");
                        }
                        if (sortParam.getProperty().equals("comment")) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "lower_comment");
                        }
                        return sortParam.ignoreCase();
                    })
                    .collect(Collectors.toList()));
        } else {
            newSort = Sort.by("version").descending();
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return TemplateFileRepository.SUPPORTED_SORT_FIELDS;
    }
}
