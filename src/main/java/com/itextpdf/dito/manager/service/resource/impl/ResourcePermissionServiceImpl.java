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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.util.StringUtils;

import java.util.List;

import static com.itextpdf.dito.manager.repository.resource.ResourcePermissionRepository.SUPPORTED_SORT_FIELDS;


@Service
public class ResourcePermissionServiceImpl extends AbstractService implements ResourcePermissionService {

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
        final ResourceEntity resourceEntity = resourceService.getResource(name, type);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final List<String> roleNameFilter = FilterUtils.getListStringsFromFilter(filter.getName());
        final String editResourceMetadataImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getE8_US55_EDIT_RESOURCE_METADATA_IMAGE());
        final String createNewVersionResourceImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE());
        final String rollBackResourceImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE());
        final String deleteResourceImage = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getE8_US66_DELETE_RESOURCE_IMAGE());
        return StringUtils.isEmpty(search)
                ? resourcePermissionRepository.filter(pageable, resourceEntity.getId(), roleNameFilter, editResourceMetadataImage,
                createNewVersionResourceImage, rollBackResourceImage, deleteResourceImage)
                : resourcePermissionRepository.search(pageable, resourceEntity.getId(), roleNameFilter, editResourceMetadataImage,
                createNewVersionResourceImage, rollBackResourceImage, deleteResourceImage, search);
    }

}
