package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.filter.FilterUtils;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionPermissionsRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionPermissionService;
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
        final List<String> roleNameFilter = filter.getRoleName();
        final String editDataCollectionMetadata = FilterUtils.getStringMultiselectFromFilter(filter.getEditDataCollectionMetadata());
        final String createNewVersionOfDataCollection = FilterUtils.getStringMultiselectFromFilter(filter.getCreateNewVersionOfDataCollection());
        final String rollbackOfTheDataCollection = FilterUtils.getStringMultiselectFromFilter(filter.getRollbackOfTheDataCollection());
        final String deleteDataCollection = FilterUtils.getStringMultiselectFromFilter(filter.getDeleteDataCollection());
        final String createNewDataSample = FilterUtils.getStringMultiselectFromFilter(filter.getCreateNewDataSample());
        final String editSampleMetadata = FilterUtils.getStringMultiselectFromFilter(filter.getEditSampleMetadata());
        final String createNewVersionOfDataSample = FilterUtils.getStringMultiselectFromFilter(filter.getCreateNewVersionOfDataSample());
        final String deleteDataSample = FilterUtils.getStringMultiselectFromFilter(filter.getDeleteDataSample());

        return StringUtils.isEmpty(search)
                ? dataCollectionPermissionsRepository.filterPermissions(pageable, name, roleNameFilter, editDataCollectionMetadata, createNewVersionOfDataCollection, rollbackOfTheDataCollection, deleteDataCollection, createNewDataSample, editSampleMetadata, createNewVersionOfDataSample, deleteDataSample)
                : dataCollectionPermissionsRepository.searchPermissions(pageable, name, roleNameFilter, editDataCollectionMetadata, createNewVersionOfDataCollection, rollbackOfTheDataCollection, deleteDataCollection, createNewDataSample, editSampleMetadata, createNewVersionOfDataSample, deleteDataSample, search.toLowerCase());
    }

    private Pageable updateSort(final Pageable pageable) {
        final Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("name")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "name");
                    }
                    if (sortParam.getProperty().equals("E6_US34_EDIT_DATA_COLLECTION_METADATA")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E6_US34_EDIT_DATA_COLLECTION_METADATA");
                    }
                    if (sortParam.getProperty().equals("E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON");
                    }
                    if (sortParam.getProperty().equals("E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION");
                    }
                    if (sortParam.getProperty().equals("E6_US38_DELETE_DATA_COLLECTION")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E6_US38_DELETE_DATA_COLLECTION");
                    }
                    if (sortParam.getProperty().equals("E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE");
                    }
                    if (sortParam.getProperty().equals("E7_US47_EDIT_SAMPLE_METADATA")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E7_US47_EDIT_SAMPLE_METADATA");
                    }
                    if (sortParam.getProperty().equals("E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE");
                    }
                    if (sortParam.getProperty().equals("E7_US50_DELETE_DATA_SAMPLE")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E7_US50_DELETE_DATA_SAMPLE");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}
