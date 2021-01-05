package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionFileService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.*;

@Service
public class DataCollectionsFileServiceImpl extends AbstractService implements DataCollectionFileService {

    private final DataCollectionService dataCollectionService;
    private final DataCollectionFileRepository dataCollectionFileRepository;

    public DataCollectionsFileServiceImpl(DataCollectionService dataCollectionService,
                                          DataCollectionFileRepository dataCollectionFileRepository) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionFileRepository = dataCollectionFileRepository;
    }

    @Override
    public Page<DataCollectionFileEntity> list(Pageable pageable, String name, VersionFilter filter, String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final DataCollectionEntity dataCollection = dataCollectionService.get(name);

        final Pageable pageWithSort = updateSort(pageable);
        final Long version = getLongFromFilter(filter.getVersion());
        final String createdBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());

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
                ? dataCollectionFileRepository.filter(pageWithSort, dataCollection.getId(), version, createdBy, createdOnStartDate, createdOnEndDate, comment)
                : dataCollectionFileRepository.search(pageWithSort, dataCollection.getId(), version, createdBy, createdOnStartDate, createdOnEndDate, comment, searchParam);
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("version")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "version");
                    }
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "author.firstName");
                    }
                    if (sortParam.getProperty().equals("modifiedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "createdOn");
                    }
                    if (sortParam.getProperty().equals("comment")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "comment");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return DataCollectionFileRepository.SUPPORTED_SORT_FIELDS;
    }

}
