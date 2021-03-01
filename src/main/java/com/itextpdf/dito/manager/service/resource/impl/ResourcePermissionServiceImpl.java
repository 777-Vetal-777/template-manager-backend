package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.FilterUtils;
import com.itextpdf.dito.manager.filter.resource.ResourcePermissionFilter;
import com.itextpdf.dito.manager.model.resource.ResourcePermissionModel;
import com.itextpdf.dito.manager.repository.resource.ResourcePermissionRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.resource.ResourcePermissionService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.repository.resource.ResourcePermissionRepository.SUPPORTED_SORT_FIELDS;


@Service
public class ResourcePermissionServiceImpl extends AbstractService implements ResourcePermissionService {
    private static final Logger log = LogManager.getLogger(ResourcePermissionServiceImpl.class);

    private ResourcePermissionRepository resourcePermissionRepository;

    private ResourceService resourceService;

    public ResourcePermissionServiceImpl(final ResourcePermissionRepository resourcePermissionRepository,
                                         final ResourceService resourceService) {
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.resourceService = resourceService;
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return SUPPORTED_SORT_FIELDS;
    }

    @Override
    public Page<ResourcePermissionModel> getRoles(final Pageable pageable, final String name, final ResourceTypeEnum type,
                                                  final ResourcePermissionFilter filter, final String search) {
        log.info("Get roles by name: {} and type: {} and filter: {} and searchParam: {} was started", name, type, filter, search);
        final ResourceEntity resourceEntity = resourceService.getResource(name, type);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final List<String> roleNameFilter = FilterUtils.getListStringsFromFilter(filter.getName());
        final String editResourceMetadataImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditResourceMetadataImage());
        final String createNewVersionResourceImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewVersionResourceImage());
        final String rollBackResourceImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollBackResourceImage());
        final String deleteResourceImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getDeleteResourceImage());
        final String editResourceMetadataFont = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditResourceMetadataFont());
        final String createNewVersionResourceFont = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewVersionResourceFont());
        final String rollBackResourceFont = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollBackResourceFont());
        final String deleteResourceFont = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getDeleteResourceFont());
        final String editResourceMetadataStylesheet = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditResourceMetadataStylesheet());
        final String createNewVersionResourceStylesheet = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewVersionResourceStylesheet());
        final String rollBackResourceStylesheet = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollBackResourceStylesheet());
        final String deleteResourceStylesheet = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getDeleteResourceStylesheet());


        final Pageable pageWithSort = updateSort(pageable);
        final Page<ResourcePermissionModel> resourcePermissionModels = StringUtils.isEmpty(search)
                ? resourcePermissionRepository.filter(pageWithSort, resourceEntity.getId(), roleNameFilter, editResourceMetadataImage,
                createNewVersionResourceImage, rollBackResourceImage, deleteResourceImage, editResourceMetadataFont, createNewVersionResourceFont,
                rollBackResourceFont, deleteResourceFont, editResourceMetadataStylesheet, createNewVersionResourceStylesheet, rollBackResourceStylesheet, deleteResourceStylesheet)
                : resourcePermissionRepository.search(pageWithSort, resourceEntity.getId(), roleNameFilter, editResourceMetadataImage,
                createNewVersionResourceImage, rollBackResourceImage, deleteResourceImage, editResourceMetadataFont, createNewVersionResourceFont,
                rollBackResourceFont, deleteResourceFont, editResourceMetadataStylesheet, createNewVersionResourceStylesheet, rollBackResourceStylesheet, deleteResourceStylesheet,
                search.toLowerCase());
        log.info("Get roles by name: {} and type: {} and filter: {} and searchParam: {} was finished successfully", name, type, filter, search);

        return resourcePermissionModels;
    }

    private Pageable updateSort(final Pageable pageable) {
        final Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (!sortParam.getProperty().equals("name")) {
                        if (sortParam.isAscending()) {
                            sortParam = new Sort.Order(Sort.Direction.DESC, sortParam.getProperty());
                        } else if (sortParam.isDescending()) {
                            sortParam = new Sort.Order(Sort.Direction.ASC, sortParam.getProperty());
                        }
                    }
                    if (sortParam.getProperty().equals("name")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "lower_name");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}
