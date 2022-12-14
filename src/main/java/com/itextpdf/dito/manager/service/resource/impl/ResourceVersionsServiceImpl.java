package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceVersionNotFoundException;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.resource.ResourceVersionsService;
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
import static com.itextpdf.dito.manager.filter.FilterUtils.getListLowerStringsForNativeFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getListLowerStringsFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromLong;

@Service
public class ResourceVersionsServiceImpl extends AbstractService implements ResourceVersionsService {
    private static final Logger log = LogManager.getLogger(ResourceVersionsServiceImpl.class);
    private final ResourceFileRepository resourceFileRepository;
    private final ResourceService resourceService;

    public ResourceVersionsServiceImpl(
            final ResourceFileRepository resourceFileRepository,
            final ResourceService resourceService) {
        this.resourceFileRepository = resourceFileRepository;
        this.resourceService = resourceService;
    }

    @Override
    public Page<FileVersionModel> list(final Pageable pageable, final String name, final ResourceTypeEnum type, final VersionFilter filter, final String searchParam) {
        log.info("Get list resource versions by resourceName: {} and type: {} and filter: {} and searchParam: {} was started", name, type, filter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final ResourceEntity resource = resourceService.getResource(name, type);

        final Pageable pageWithSort = updateSort(pageable);
        final String version = getStringFromLong(filter.getVersion());
        final String modifiedBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());
        final List<String> stageName = getListLowerStringsForNativeFromFilter(filter.getStage());

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
        final Page<FileVersionModel> fileVersionModels = StringUtils.isEmpty(searchParam)
                ? resourceFileRepository.filter(pageWithSort, resource.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, stageName)
                : resourceFileRepository.search(pageWithSort, resource.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, stageName, searchParam.toLowerCase());

        log.info("Get list resource versions by resourceName: {} and type: {} and filter: {} and searchParam: {} was finished successfully", name, type, filter, searchParam);
        return fileVersionModels;
    }

    @Override
    public ResourceEntity rollbackVersion(final String resourceName, final ResourceTypeEnum resourceType, final String userEmail, final Long version) {
        log.info("Rollback resource version by resourceName: {} and type: {} and email: {} and version: {} was started", resourceName, resourceType, userEmail, version);
        final ResourceEntity resourceEntity = resourceService.getResource(resourceName, resourceType);
        final ResourceFileEntity resourceFileEntityToBeRevertedTo = resourceFileRepository.findByVersionAndResource(version, resourceEntity)
                .orElseThrow(() -> new ResourceVersionNotFoundException(String.valueOf(version)));
        final String comment = new StringBuilder().append("Rollback to version: ").append(resourceFileEntityToBeRevertedTo.getVersion()).toString();
        final ResourceEntity savedResourceEntity = resourceService.createNewVersion(resourceName, resourceType, resourceFileEntityToBeRevertedTo.getFile(), resourceFileEntityToBeRevertedTo.getFileName(), userEmail, comment);
        log.info("Rollback resource version by resourceName: {} and type: {} and email: {} and version: {} was finished successfully", resourceName, resourceType, userEmail, version);
        return savedResourceEntity;
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
    public List<String> getSupportedSortFields() {
        return ResourceFileRepository.SUPPORTED_SORT_FIELDS;
    }
}
