package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionFileService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
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
import java.util.Map;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getListLowerStringsFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class DataCollectionsFileServiceImpl extends AbstractService implements DataCollectionFileService {
    private static final Logger log = LogManager.getLogger(DataCollectionsFileServiceImpl.class);

    private final DataCollectionService dataCollectionService;
    private final DataCollectionFileRepository dataCollectionFileRepository;

    public DataCollectionsFileServiceImpl(final DataCollectionService dataCollectionService,
                                          final DataCollectionFileRepository dataCollectionFileRepository) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionFileRepository = dataCollectionFileRepository;
    }

    @Override
    public Page<FileVersionModel> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam) {
        log.info("Get dataCollection versions by name: {} and filter: {} and searchParam: {} was started", name, filter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final DataCollectionEntity dataCollection = dataCollectionService.get(name);

        final Pageable pageWithSort = updateSort(pageable);
        final Long version = getLongFromFilter(filter.getVersion());
        final String createdBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());
        final List<String> stageName = getListLowerStringsFromFilter(filter.getStage());

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
        final Page<FileVersionModel> versions = StringUtils.isEmpty(searchParam)
                ? dataCollectionFileRepository.filter(pageWithSort, dataCollection.getId(), version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment)
                : dataCollectionFileRepository.search(pageWithSort, dataCollection.getId(), version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment, searchParam.toLowerCase());
        log.info("Get dataCollection versions by name: {} and filter: {} and searchParam: {} was finished successfully", name, filter, searchParam);
        return versions;
    }

    private Pageable updateSort(final Pageable pageable) {
        final Map<String, String> orderRenamingValues = Map.of(
                "modifiedBy", "author.firstName",
                "modifiedOn", "createdOn",
                "comment", "comment",
                "stage", "stage.name");

        Sort newSort;
        if (pageable.getSort().isSorted()) {
            newSort = Sort.by(pageable.getSort().stream()
                    .map(sortParam -> {
                        if (orderRenamingValues.containsKey(sortParam.getProperty())) {
                            sortParam = new Sort.Order(sortParam.getDirection(), orderRenamingValues.get(sortParam.getProperty()));
                        }
                        return sortParam.getProperty().equals("createdOn") ? sortParam : sortParam.ignoreCase();
                    })
                    .collect(Collectors.toList()));
        } else {
            newSort = Sort.by("version").descending();
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return DataCollectionFileRepository.SUPPORTED_SORT_FIELDS;
    }

}
