package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.filter.FilterUtils;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;
import com.itextpdf.dito.manager.repository.template.TemplatePermissionRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.template.TemplatePermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.repository.template.TemplatePermissionRepository.SUPPORTED_SORT_FIELDS;

@Service
public class TemplatePermissionServiceImpl extends AbstractService implements TemplatePermissionService {

    private final TemplatePermissionRepository templatePermissionRepository;

    public TemplatePermissionServiceImpl(final TemplatePermissionRepository templatePermissionRepository) {
        this.templatePermissionRepository = templatePermissionRepository;
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return SUPPORTED_SORT_FIELDS;
    }

    @Override
    public Page<TemplatePermissionsModel> getRoles(final Pageable pageable, final String name, final TemplatePermissionFilter filter, final String search) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final List<String> roleNameFilter = FilterUtils.getListStringsFromFilter(filter.getName());
        final String editTemplateMetadataFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditTemplateMetadata());
        final String createNewTemplateVersionStandardFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewTemplateVersionStandard());
        final String rollbackVersionStandardFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollbackVersionStandard());
        final String previewTemplateFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getPreviewTemplate());
        final String exportTemplateFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getExportTemplate());
        final String createNewTemplateVersionCompositionFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewTemplateVersionComposition());
        final String rollbackVersionCompositionFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollbackVersionComposition());

        final Pageable pageWithSort = updateSort(pageable);

        return StringUtils.isEmpty(search)
                ? templatePermissionRepository.filter(pageWithSort, name, roleNameFilter, editTemplateMetadataFilter, createNewTemplateVersionStandardFilter, rollbackVersionStandardFilter, previewTemplateFilter, exportTemplateFilter,
                createNewTemplateVersionCompositionFilter, rollbackVersionCompositionFilter)
                : templatePermissionRepository.search(pageWithSort, name, roleNameFilter, editTemplateMetadataFilter, createNewTemplateVersionStandardFilter, rollbackVersionStandardFilter, previewTemplateFilter, exportTemplateFilter,
                createNewTemplateVersionCompositionFilter, rollbackVersionCompositionFilter, search.toLowerCase());
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
