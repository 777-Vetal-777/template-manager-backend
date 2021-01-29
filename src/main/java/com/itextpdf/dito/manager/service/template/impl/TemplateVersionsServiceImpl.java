package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.template.TemplateVersionsService;
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
import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class TemplateVersionsServiceImpl extends AbstractService implements TemplateVersionsService {
    private final TemplateService templateService;
    private final TemplateFileRepository templateFileRepository;

    public TemplateVersionsServiceImpl(final TemplateService templateService,
                                       final TemplateFileRepository templateFileRepository) {
        this.templateService = templateService;
        this.templateFileRepository = templateFileRepository;
    }

    @Override
    public Page<FileVersionModel> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final TemplateEntity template = templateService.get(name);

        final Pageable pageWithSort = updateSort(pageable);
        final Long version = getLongFromFilter(filter.getVersion());
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
        return StringUtils.isEmpty(searchParam)
                ? templateFileRepository.filter(pageWithSort, template.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, stageName)
                : templateFileRepository.search(pageWithSort, template.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, stageName, searchParam.toLowerCase());
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
                        return sortParam;
                    })
                    .collect(Collectors.toList()));
        } else {
            newSort = Sort.by("version").ascending();
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return TemplateFileRepository.SUPPORTED_SORT_FIELDS;
    }
}
