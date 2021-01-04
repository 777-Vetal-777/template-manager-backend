package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.resource.dependency.ResourceDependencyFilter;
import com.itextpdf.dito.manager.model.resource.ResourceDependencyModel;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.resource.ResourceDependencyService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType.HARD;
import static com.itextpdf.dito.manager.dto.dependency.DependencyType.IMAGE;
import static com.itextpdf.dito.manager.filter.FilterUtils.getBooleanMultiselectFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class ResourceDependencyServiceImpl extends AbstractService implements ResourceDependencyService {
    private final ResourceService resourceService;
    private final ResourceFileRepository resourceFileRepository;

    public ResourceDependencyServiceImpl(
            final ResourceService resourceService,
            final ResourceFileRepository resourceFileRepository) {
        this.resourceService = resourceService;
        this.resourceFileRepository = resourceFileRepository;
    }

    @Override
    public Page<ResourceDependencyModel> list(final Pageable pageable, final String name, final ResourceTypeEnum type, final ResourceDependencyFilter filter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final List<DependencyType> dependenciesType = filter.getDependencyType();
        final List<DependencyDirectionType> directionsType = filter.getDirectionType();
        if ((dependenciesType.isEmpty() || dependenciesType.contains(IMAGE)) &&
                (directionsType.isEmpty()) || directionsType.contains(HARD)) {
            final ResourceEntity resourceEntity = resourceService.getResource(name, type);
            final Pageable pageWithSort = updateSort(pageable);
            final Long version = getLongFromFilter(filter.getVersion());
            final String depend = getStringFromFilter(filter.getName());
            final Boolean deployed = getBooleanMultiselectFromFilter(filter.getActive());

            return StringUtils.isEmpty(searchParam)
                    ? resourceFileRepository.filter(pageWithSort, resourceEntity.getId(), depend, version, type, deployed)
                    : resourceFileRepository.search(pageWithSort, resourceEntity.getId(), depend, version, type, deployed, searchParam);

        }
        return Page.empty();
    }

    @Override
    public List<ResourceDependencyModel> list(final String name, final ResourceTypeEnum type) {
        final ResourceEntity resourceEntity = resourceService.getResource(name, type);
        return resourceFileRepository.search(resourceEntity.getId());
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("name")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "templates.name");
                    }
                    if (sortParam.getProperty().equals("version")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.version");
                    }
                    if (sortParam.getProperty().equals("directionType")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.resource.type");
                    }
                    if (sortParam.getProperty().equals("dependencyType")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.resource.type");
                    }
                    if (sortParam.getProperty().equals("active")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.deployed");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    public List<String> getSupportedSortFields() {
        return ResourceFileRepository.SUPPORTED_DEPENDENCY_SORT_FIELDS;
    }
}