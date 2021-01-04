package com.itextpdf.dito.manager.service.permission.impl;

import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.exception.permission.PermissionNotFoundException;
import com.itextpdf.dito.manager.repository.permission.PermissionRepository;
import com.itextpdf.dito.manager.service.permission.PermissionService;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(final PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PermissionEntity get(final String name) {
        return permissionRepository.findByName(name).orElseThrow(() -> new PermissionNotFoundException(name));
    }

    @Override
    public Page<PermissionEntity> list(final Pageable pageable, final String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? permissionRepository.findAll(pageable)
                : permissionRepository.search(pageable, searchParam);
    }

    @Override
    public List<PermissionEntity> list() {
        return permissionRepository.findAll();
    }

    @Override
    public List<PermissionEntity> defaultPermissions() {
        final List<PermissionEntity> permissionEntities = permissionRepository.findByNameIn(defaultPermissionNames);
        if (permissionEntities.size() != defaultPermissionNames.size()) {
            throw new IllegalStateException("De-synchronization of default permission names between jvm and database.");
        }
        return permissionEntities;
    }

    private static final List<String> defaultPermissionNames = Arrays.asList(
            "E1_US1_LOG_IN_TO_THE_SYSTEM",
            "E1_US2_LOG_OUT_FROM_THE_SYSTEM",
            "E1_US3_FORGOT_PASSWORD",
            "E2_US4_GENERAL_NAVIGATIONAL_PANEL",
            "E2_US5_HEADER_PANEL",
            "E2_US6_SETTINGS_PANEL",
            "E2_US7_RECENT_ITEMS",
            "E2_US8_NOTIFICATIONS",
            "E6_US30_TABLE_OF_DATA_COLLECTIONS",
            "E6_US31_DATA_COLLECTIONS_NAVIGATION_MENU",
            "E6_US33_VIEW_DATA_COLLECTION_METADATA",
            "E6_US36_DATA_COLLECTION_VERSION_HISTORY",
            "E6_US39_TABLE_OF_DATA_COLLECTIONS_PERMISSIONS",
            "E6_US41_TABLE_OF_DATA_COLLECTION_DEPENDENCIES",
            "E7_US42_TABLE_OF_DATA_SAMPLES",
            "E7_US43_DATA_SAMPLE_NAVIGATION_MENU",
            "E7_US46_VIEW_SAMPLE_METADATA",
            "E7_US49_DATA_SAMPLE_VERSION_HISTORY",
            "E8_US52_TABLE_OF_RESOURCES",
            "E8_US101_RESOURCE_NAVIGATION_MENU",
            "E8_US54_VIEW_RESOURCE_METADATA_IMAGE",
            "E8_US57_VIEW_RESOURCE_METADATA_FONT",
            "E8_US60_VIEW_RESOURCE_METADATA_STYLESHEET",
            "E8_US_64_RESOURCE_VERSIONS_HISTORY_IMAGE",
            "E8_US67_TABLE_OF_RESOURCE_PERMISSIONS_IMAGE",
            "E8_US69_TABLE_OF_THE_RESOURCE_DEPENDENCIES_IMAGE",
            "E9_US70_TEMPLATES_TABLE",
            "E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD",
            "E9_US74_VIEW_TEMPLATE_METADATA_STANDARD",
            "E9_US78_TEMPLATE_VERSIONS_HISTORY_STANDARD",
            "E9_US82_TEMPLATE_OF_TEMPLATE_PERMISSIONS_STANDARD",
            "E9_US84_TABLE_OF_TEMPLATE_DEPENDENCIES",
            "E10_US85_USER_PROFILE",
            "E10_US86_CHANGE_PASSWORD",
            "E10_US87_PERSONAL_PREFERENCES");
}
