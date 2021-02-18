package com.itextpdf.dito.manager.service.datasample.impl;

import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.repository.datasample.DataSampleFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datasample.DataSampleFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromLong;

@Component
public class DataSampleFileServiceImpl extends AbstractService implements DataSampleFileService {
    private DataSampleFileRepository dataSampleFileRepository;

    @Autowired
    public DataSampleFileServiceImpl(final DataSampleFileRepository dataSampleFileRepository) {
        this.dataSampleFileRepository = dataSampleFileRepository;
    }

    @Override
    public Page<FileVersionModel> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final Pageable pageWithSort = updateSort(pageable);
        final String version = getStringFromLong(filter.getVersion());
        final String createdBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());
        final String stageName = getStringFromFilter(filter.getStage());

        Date createdOnStartDate = null;
        Date createdOnEndDate = null;
        final List<String> createdOnDateRange = filter.getModifiedOn();
        if (createdOnDateRange != null) {
            if (createdOnDateRange.size() != 2) {
                throw new IllegalArgumentException("Date range should contain two elements: start date and end date");
            }
            createdOnStartDate = getStartDateFromRange(createdOnDateRange);
            createdOnEndDate = getEndDateFromRange(createdOnDateRange);
        }

        return StringUtils.isEmpty(searchParam)
                ? dataSampleFileRepository.filter(pageWithSort, name, version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment)
                : dataSampleFileRepository.search(pageWithSort, name, version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment, searchParam.toLowerCase());
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort;
        if (pageable.getSort().isSorted()) {
            newSort = Sort.by(pageable.getSort().stream()
                    .map(sortParam -> {
                        if (sortParam.getProperty().equals("modifiedBy")) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "lower_modifiedBy");
                        }
                        if (sortParam.getProperty().equals("stage")) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "lower_stage");
                        }
                        if (sortParam.getProperty().equals("comment")) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "lower_comment");
                        }
                        return sortParam;
                    })
                    .collect(Collectors.toList()));
        } else {
            newSort = Sort.by("version").descending();
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return dataSampleFileRepository.SUPPORTED_SORT_FIELDS;
    }

}