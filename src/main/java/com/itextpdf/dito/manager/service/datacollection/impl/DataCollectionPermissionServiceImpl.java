package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.filter.FilterUtils;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionPermissionsRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionPermissionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataCollectionPermissionServiceImpl extends AbstractService implements DataCollectionPermissionService {
    private static final Logger log = LogManager.getLogger(DataCollectionPermissionServiceImpl.class);
    private final DataCollectionPermissionsRepository dataCollectionPermissionsRepository;

    public DataCollectionPermissionServiceImpl(final DataCollectionPermissionsRepository dataCollectionPermissionsRepository) {
        this.dataCollectionPermissionsRepository = dataCollectionPermissionsRepository;
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return dataCollectionPermissionsRepository.SUPPORTED_SORT_PERMISSION_FIELDS;
    }

    @Override
    public Page<DataCollectionPermissionsModel> getRoles(final Pageable pageable, final String name, final DataCollectionPermissionFilter filter, final String search) {
        log.info("Get roles by dataCollectionName: {} and filter: {} and searchParam: {} was started", name, filter, search);
        final List<String> roleNameFilter = FilterUtils.getListLowerStringsForNativeFromFilter(filter.getName());
        final String editDataCollectionMetadata = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditDataCollectionMetadata());
        final String createNewVersionOfDataCollection = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewVersionOfDataCollection());
        final String rollbackOfTheDataCollection = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollbackOfTheDataCollection());
        final String deleteDataCollection = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getDeleteDataCollection());
        final String createNewDataSample = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewDataSample());
        final String editSampleMetadata = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditSampleMetadata());
        final String createNewVersionOfDataSample = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewVersionOfDataSample());
        final String deleteDataSample = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getDeleteDataSample());

        final Pageable pageWithSort = updateSort(pageable);
        log.info("Get roles by dataCollectionName: {} and filter: {} and searchParam: {} was finished successfully", name, filter, search);
        return StringUtils.isEmpty(search)
                ? dataCollectionPermissionsRepository.filterPermissions(pageWithSort, name, roleNameFilter, editDataCollectionMetadata, createNewVersionOfDataCollection, rollbackOfTheDataCollection, deleteDataCollection, createNewDataSample, editSampleMetadata, createNewVersionOfDataSample, deleteDataSample)
                : dataCollectionPermissionsRepository.searchPermissions(pageWithSort, name, roleNameFilter, editDataCollectionMetadata, createNewVersionOfDataCollection, rollbackOfTheDataCollection, deleteDataCollection, createNewDataSample, editSampleMetadata, createNewVersionOfDataSample, deleteDataSample, search.toLowerCase());
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
