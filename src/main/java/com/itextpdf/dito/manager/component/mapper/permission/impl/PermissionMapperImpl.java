package com.itextpdf.dito.manager.component.mapper.permission.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.dto.permission.DataCollectionPermissionDTO;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapperImpl implements PermissionMapper {
    @Override
    public PermissionDTO map(final PermissionEntity entity) {
        final PermissionDTO result = new PermissionDTO();

        result.setName(entity.getName());
        result.setOptionalForCustomRole(entity.getOptionalForCustomRole());

        return result;
    }

    @Override
    public PermissionEntity map(final PermissionDTO dto) {
        final PermissionEntity result = new PermissionEntity();

        result.setName(dto.getName());
        result.setOptionalForCustomRole(dto.getOptionalForCustomRole());

        return result;
    }

    @Override
    public List<PermissionDTO> map(Collection<PermissionEntity> entities) {
        return entities != null
                ? entities.stream().map(this::map).collect(Collectors.toList())
                : Collections.emptyList();
    }

    @Override
    public Page<PermissionDTO> map(final Page<PermissionEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public List<TemplatePermissionDTO> mapTemplatePermissions(final List<TemplatePermissionsModel> entities) {
        return entities.stream().map(this::mapTemplatePermission).collect(Collectors.toList());
    }

    @Override
    public Page<TemplatePermissionDTO> mapTemplatePermissions(final Page<TemplatePermissionsModel> entities) {
        return entities.map(this::mapTemplatePermission);
    }

    private TemplatePermissionDTO mapTemplatePermission(final TemplatePermissionsModel entity) {
        final TemplatePermissionDTO templatePermissionDTO = new TemplatePermissionDTO();

        templatePermissionDTO.setName(entity.getName());
        templatePermissionDTO.setExportTemplatePermission(entity.getE9_US24_EXPORT_TEMPLATE_DATA());
        templatePermissionDTO.setPreviewTemplatePermission(entity.getE9_US81_PREVIEW_TEMPLATE_STANDARD());
        templatePermissionDTO.setRollbackPermission(entity.getE9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE());
        templatePermissionDTO.setEditMetadataPermission(entity.getE9_US75_EDIT_TEMPLATE_METADATA_STANDARD());
        templatePermissionDTO.setCreateNewVersionPermission(entity.getE9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD());

        return templatePermissionDTO;
    }


    @Override
    public Page<DataCollectionPermissionDTO> mapDataCollectionPermissions(final Page<DataCollectionPermissionsModel> entities) {
        return entities.map(this::mapDataCollectionPermission);
    }

    private DataCollectionPermissionDTO mapDataCollectionPermission(final DataCollectionPermissionsModel entity) {
        final DataCollectionPermissionDTO permissionDTO = new DataCollectionPermissionDTO();
        permissionDTO.setRoleName(entity.getName());
        permissionDTO.setE6_US34_EDIT_DATA_COLLECTION_METADATA(entity.getE6_US34_EDIT_DATA_COLLECTION_METADATA());
        permissionDTO.setE6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION(entity.getE6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION());
        permissionDTO.setE6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON(entity.getE6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON());
        permissionDTO.setE6_US38_DELETE_DATA_COLLECTION(entity.getE6_US38_DELETE_DATA_COLLECTION());
        permissionDTO.setE7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE(entity.getE7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE());
        permissionDTO.setE7_US50_DELETE_DATA_SAMPLE(entity.getE7_US50_DELETE_DATA_SAMPLE());
        permissionDTO.setE7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE(entity.getE7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE());
        permissionDTO.setE7_US47_EDIT_SAMPLE_METADATA(entity.getE7_US47_EDIT_SAMPLE_METADATA());

        return permissionDTO;
    }

    private void addPermission(final String condition, final String result, final List<PermissionDTO> permissions) {
        if (Boolean.parseBoolean(condition)) {
            final PermissionDTO permission = new PermissionDTO();
            permission.setName(result);
            permissions.add(permission);
        }
    }
}
