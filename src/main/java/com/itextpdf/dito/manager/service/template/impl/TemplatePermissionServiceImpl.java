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

        final List<String> roleNameFilter = FilterUtils.getListStringsFromFilter(filter.getRoleName());
        final String editTemplateMetadataFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditTemplateMetadata());
        final String createNewTemplateVersionFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewTemplateVersion());
        final String rollbackVersionFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollbackVersion());
        final String previewTemplateFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getPreviewTemplate());
        final String exportTemplateFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getExportTemplate());

        final Pageable pageWithSort = updateSort(pageable);
        
        return StringUtils.isEmpty(search)
                ? templatePermissionRepository.filter(pageWithSort, name, roleNameFilter, editTemplateMetadataFilter, createNewTemplateVersionFilter, rollbackVersionFilter, previewTemplateFilter, exportTemplateFilter)
                : templatePermissionRepository.search(pageWithSort, name, roleNameFilter, editTemplateMetadataFilter, createNewTemplateVersionFilter, rollbackVersionFilter, previewTemplateFilter, exportTemplateFilter, search.toLowerCase());
    }

    @Override
    public List<TemplatePermissionsModel> getRoles(final String name, final TemplatePermissionFilter filter, String search) {
        final List<String> roleNameFilter = filter.getRoleName();
        final String editTemplateMetadataFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getEditTemplateMetadata());
        final String createNewTemplateVersionFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getCreateNewTemplateVersion());
        final String rollbackVersionFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getRollbackVersion());
        final String previewTemplateFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getPreviewTemplate());
        final String exportTemplateFilter = FilterUtils.getStringFromMultiselectBooleanFilter(filter.getExportTemplate());

        return StringUtils.isEmpty(search)
                ? templatePermissionRepository.filter(name, roleNameFilter, editTemplateMetadataFilter, createNewTemplateVersionFilter, rollbackVersionFilter, previewTemplateFilter, exportTemplateFilter)
                : templatePermissionRepository.search(name, roleNameFilter, editTemplateMetadataFilter, createNewTemplateVersionFilter, rollbackVersionFilter, previewTemplateFilter, exportTemplateFilter, search.toLowerCase());
    }

    private Pageable updateSort(final Pageable pageable) {
        final Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("name")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "name");
                    }
                    if (sortParam.getProperty().equals("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E9_US75_EDIT_TEMPLATE_METADATA_STANDARD");
                    }
                    if (sortParam.getProperty().equals("E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD");
                    }
                    if (sortParam.getProperty().equals("E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE");
                    }
                    if (sortParam.getProperty().equals("E9_US81_PREVIEW_TEMPLATE_STANDARD")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E9_US81_PREVIEW_TEMPLATE_STANDARD");
                    }
                    if (sortParam.getProperty().equals("E9_US24_EXPORT_TEMPLATE_DATA")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "E9_US24_EXPORT_TEMPLATE_DATA");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

}
