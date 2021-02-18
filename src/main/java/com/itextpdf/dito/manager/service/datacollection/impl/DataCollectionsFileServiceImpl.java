package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionFileService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.template.TemplateService;
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
public class DataCollectionsFileServiceImpl extends AbstractService implements DataCollectionFileService {

    private final DataCollectionService dataCollectionService;
    private final DataCollectionFileRepository dataCollectionFileRepository;
    private final TemplateService templateService;

    public DataCollectionsFileServiceImpl(final DataCollectionService dataCollectionService,
                                          final DataCollectionFileRepository dataCollectionFileRepository,
                                          final TemplateService templateService) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionFileRepository = dataCollectionFileRepository;
        this.templateService = templateService;
    }

    @Override
    public Page<FileVersionModel> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final DataCollectionEntity dataCollection = dataCollectionService.get(name);

        final Pageable pageWithSort = updateSort(pageable);
        final Long version = getLongFromFilter(filter.getVersion());
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
                ? dataCollectionFileRepository.filter(pageWithSort, dataCollection.getId(), version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment)
                : dataCollectionFileRepository.search(pageWithSort, dataCollection.getId(), version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment, searchParam.toLowerCase());
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort;
        if (pageable.getSort().isSorted()) {
            newSort = Sort.by(pageable.getSort().stream()
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
                        if (sortParam.getProperty().equals("stage")) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "stage.name");
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

    @Override
    public DataCollectionFileEntity getByTemplateName(final String name) {
        return templateService.get(name).getLatestFile().getDataCollectionFile();//TODO chance to get NPE
    }

}
