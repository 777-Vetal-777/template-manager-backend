package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionDependencyService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class DataCollectionDependencyServiceImpl extends AbstractService implements DataCollectionDependencyService {
    private static final Logger log = LogManager.getLogger(DataCollectionDependencyServiceImpl.class);
    private final DataCollectionService dataCollectionService;
    private final DataCollectionFileRepository dataCollectionFileRepository;

    public DataCollectionDependencyServiceImpl(final DataCollectionService dataCollectionService,
                                               final DataCollectionFileRepository dataCollectionFileRepository) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionFileRepository = dataCollectionFileRepository;
    }

    @Override
    public Page<DependencyModel> list(final Pageable pageable,
                                      final String name,
                                      final DependencyFilter filter,
                                      final String searchParam) {
        log.info("Get list dataCollection dependencies with pageable by name {} and filter: {} and searchParam: {} was started", name, filter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        Page<DependencyModel> searchResult = Page.empty();
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(name);
        final Pageable pageWithSort = updateSort(pageable);
        final Long version = getLongFromFilter(filter.getVersion());
        final String depend = getStringFromFilter(filter.getName());
        final List<String> stages = filter.getStage();
        final List<String> directionType = filter.getDirectionType() != null ? filter.getDirectionType().stream().map(d -> d.toString().toLowerCase()).collect(Collectors.toList()) : Collections.emptyList();
        final Boolean isSearchEmpty = StringUtils.isEmpty(searchParam);
        if (Objects.isNull(filter.getDependencyType()) || filter.getDependencyType().contains(DependencyType.TEMPLATE)) {
            searchResult = isSearchEmpty
                    ? dataCollectionFileRepository.filter(pageWithSort, dataCollectionEntity.getId(), depend, version, directionType, stages)
                    : dataCollectionFileRepository.search(pageWithSort, dataCollectionEntity.getId(), depend, version, directionType, stages, searchParam.toLowerCase());
        }
        log.info("Get list dataCollection dependencies with pageable by name {} and filter: {} and searchParam: {} was finished successfully", name, filter, searchParam);
        return searchResult;

    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .flatMap(sortParam -> {
                    //no need to sort by these fields
                    if ("dependencyType".equals(sortParam.getProperty()) || "directionType".equals(sortParam.getProperty())) {
                        return Stream.empty();
                    }
                    if ("version".equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "lastTemplateFile.version");
                    }
                    if ("stage".equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "stage.name").ignoreCase();
                    }
                    if ("name".equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "name").ignoreCase();
                    }
                    return Stream.of(sortParam);
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return DataCollectionFileRepository.SUPPORTED_DEPENDENCY_SORT_FIELDS;
    }

    @Override
    public List<DependencyModel> list(final String name) {
        log.info("Get list dataCollection dependencies with pageable by name {} was started", name);
        final DataCollectionEntity existingDataCollection = dataCollectionService.get(name);
        final List<DependencyModel> dependencyModelList = dataCollectionFileRepository.searchDependencyOfDataCollection(existingDataCollection.getId());
        log.info("Get list dataCollection dependencies with pageable by name {} was finished successfully", name);
        return dependencyModelList;
    }

}
