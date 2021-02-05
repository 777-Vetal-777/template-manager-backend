package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType.HARD;
import static com.itextpdf.dito.manager.dto.dependency.DependencyType.TEMPLATE;
import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class ResourceDependencyServiceImpl extends AbstractService implements ResourceDependencyService {
    private static final String HARD_DEPENDENCY = HARD.toString().toLowerCase();
    private static final String TEMPLATE_DEPENDENCY_TYPE = TEMPLATE.toString().toLowerCase();
    private final ResourceService resourceService;
    private final ResourceFileRepository resourceFileRepository;

    public ResourceDependencyServiceImpl(
            final ResourceService resourceService,
            final ResourceFileRepository resourceFileRepository) {
        this.resourceService = resourceService;
        this.resourceFileRepository = resourceFileRepository;
    }

    @Override
    public Page<DependencyModel> list(final Pageable pageable, final String name, final ResourceTypeEnum type, final DependencyFilter filter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        Page<DependencyModel> searchResult = Page.empty();
        final List<DependencyType> dependenciesType = filter.getDependencyType();
        final List<DependencyDirectionType> directionsType = filter.getDirectionType();
        if ((Objects.isNull(dependenciesType) || dependenciesType.contains(TEMPLATE)) &&
                (Objects.isNull(directionsType) || directionsType.contains(HARD))) {
            final ResourceEntity resourceEntity = resourceService.getResource(name, type);
            final Pageable pageWithSort = updateSort(pageable);
            final Long version = getLongFromFilter(filter.getVersion());
            final String depend = getStringFromFilter(filter.getName());
            final String stage = getStringFromFilter(filter.getStage());
            final Boolean isSearchEmpty = StringUtils.isEmpty(searchParam);
            //a condition if the search contains a resource of type - image, or a HARD dependence. Because all dependencies in this case are a IMAGE or a HARD
            if (!isSearchEmpty && (HARD_DEPENDENCY.contains(searchParam.toLowerCase()) || TEMPLATE_DEPENDENCY_TYPE.contains(searchParam.toLowerCase()))) {
                searchResult = resourceFileRepository.filter(pageWithSort, resourceEntity.getId(), depend, version, stage);
            } else {
                searchResult = isSearchEmpty
                        ? resourceFileRepository.filter(pageWithSort, resourceEntity.getId(), depend, version, stage)
                        : resourceFileRepository.search(pageWithSort, resourceEntity.getId(), depend, version, stage, searchParam.toLowerCase());
            }
        }
        return searchResult;
    }

    @Override
    public List<DependencyModel> list(final String name, final ResourceTypeEnum type) {
        final ResourceEntity resourceEntity = resourceService.getResource(name, type);
        return resourceFileRepository.searchDependencies(resourceEntity.getId());
    }

    private Pageable updateSort(final Pageable pageable) {
        final Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("name")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "name");
                    }
                    if (sortParam.getProperty().equals("version")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "version");
                    }
                    if (sortParam.getProperty().equals("directionType")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "template.name");
                    }
                    if (sortParam.getProperty().equals("dependencyType")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "template.name");
                    }
                    if (sortParam.getProperty().equals("stage")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "stage");
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