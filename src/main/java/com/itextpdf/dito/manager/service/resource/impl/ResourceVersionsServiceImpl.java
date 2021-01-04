package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.resource.ResourceVersionsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getBooleanMultiselectFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class ResourceVersionsServiceImpl extends AbstractService implements ResourceVersionsService {
    private final ResourceFileRepository resourceFileRepository;
    private final ResourceService resourceService;

    public ResourceVersionsServiceImpl(
            final ResourceFileRepository resourceFileRepository,
            final ResourceService resourceService) {
        this.resourceFileRepository = resourceFileRepository;
        this.resourceService = resourceService;
    }

    @Override
    public Page<ResourceFileEntity> list(final Pageable pageable, final String name, final ResourceTypeEnum type, final VersionFilter filter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final ResourceEntity resource = resourceService.getResource(name, type);

        final Pageable pageWithSort = updateSort(pageable);
        final Long version = getLongFromFilter(filter.getVersion());
        final String modifiedBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());
        final Boolean deployed = getBooleanMultiselectFromFilter(filter.getDeployed());

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
                ? resourceFileRepository.filter(pageWithSort, resource.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, deployed)
                : resourceFileRepository.search(pageWithSort, resource.getId(), version, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, comment, deployed, searchParam);
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.author.firstName");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    public List<String> getSupportedSortFields() {
        return ResourceFileRepository.SUPPORTED_SORT_FIELDS;
    }
}
