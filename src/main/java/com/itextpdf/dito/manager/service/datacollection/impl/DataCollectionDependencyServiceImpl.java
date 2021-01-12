package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionDependencyFilter;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionDependencyService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class DataCollectionDependencyServiceImpl extends AbstractService implements DataCollectionDependencyService {
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
                                      final DataCollectionDependencyFilter filter,
                                      final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        Page<DependencyModel> searchResult = Page.empty();
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(name);
        final Pageable pageWithSort = updateSort(pageable);
        final Long version = getLongFromFilter(filter.getVersion());
        final String depend = getStringFromFilter(filter.getDependencyName());
        final String stageName = getStringFromFilter(filter.getStageName());
        final Boolean isSearchEmpty = StringUtils.isEmpty(searchParam);
        if (Objects.isNull(filter.getDependencyType()) || filter.getDependencyType().contains(DependencyType.TEMPLATE)) {
            searchResult = isSearchEmpty
                    ? dataCollectionFileRepository.filter(pageWithSort, dataCollectionEntity.getId(), depend, version, stageName)
                    : dataCollectionFileRepository.search(pageWithSort, dataCollectionEntity.getId(), depend, version, stageName, searchParam);
        }
        return searchResult;

    }


    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("name")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "name");
                    }
                    if (sortParam.getProperty().equals("version")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.version");
                    }
                    if (sortParam.getProperty().equals("stage")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "stage.name");
                    }
                    return sortParam;
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
        final DataCollectionEntity existingDataCollection = dataCollectionService.get(name);
        return dataCollectionFileRepository.searchDependencyOfDataCollection(existingDataCollection.getId());
    }

}
