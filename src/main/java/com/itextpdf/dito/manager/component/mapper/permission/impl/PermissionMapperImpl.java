package com.itextpdf.dito.manager.component.mapper.permission.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.dto.permission.DataCollectionPermissionDTO;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.dto.permission.ResourcePermissionDTO;
import com.itextpdf.dito.manager.dto.permission.ResourcePermissionFontDTO;
import com.itextpdf.dito.manager.dto.permission.ResourcePermissionImageDTO;
import com.itextpdf.dito.manager.dto.permission.ResourcePermissionStylesheetDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionCompositionDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionStandardDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.dito.manager.model.resource.ResourcePermissionModel;
import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapperImpl implements PermissionMapper {
    private static final Logger log = LogManager.getLogger(PermissionMapperImpl.class);

    @Override
    public PermissionDTO map(final PermissionEntity entity) {
        log.info("Convert permissionEntity: {} to dto was started", entity.getId());
        final PermissionDTO result = new PermissionDTO();

        result.setName(entity.getName());
        result.setOptionalForCustomRole(entity.getOptionalForCustomRole());
        log.info("Convert permissionEntity: {} to dto was finished successfully", entity.getId());

        return result;
    }

    @Override
    public PermissionEntity map(final PermissionDTO dto) {
        log.info("Convert {} to entity was started", dto);
        final PermissionEntity result = new PermissionEntity();

        result.setName(dto.getName());
        result.setOptionalForCustomRole(dto.getOptionalForCustomRole());
        log.info("Convert {} to entity was finished successfully", dto);

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
    public Page<TemplatePermissionDTO> mapTemplatePermissions(final Page<TemplatePermissionsModel> entities) {
        return entities.map(this::mapTemplatePermission);
    }

    private TemplatePermissionDTO mapTemplatePermission(final TemplatePermissionsModel entity) {
        log.info("Convert {} to templatePermission dto was started", entity);
         TemplatePermissionDTO templatePermissionDTO = null;
        if(entity.getTemplateType().equals("STANDARD")||entity.getTemplateType().equals("HEADER") || entity.getTemplateType().equals("FOOTER")){
            templatePermissionDTO = new TemplatePermissionStandardDTO();
            templatePermissionDTO.setExportTemplatePermission(entity.getE9_US24_EXPORT_TEMPLATE_DATA());
            templatePermissionDTO.setPreviewTemplatePermission(entity.getE9_US81_PREVIEW_TEMPLATE_STANDARD());
            templatePermissionDTO.setRollbackPermission(entity.getE9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE());
            templatePermissionDTO.setEditMetadataPermission(entity.getE9_US75_EDIT_TEMPLATE_METADATA_STANDARD());
            templatePermissionDTO.setCreateNewVersionPermission(entity.getE9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD());
        }else if(entity.getTemplateType().equals("COMPOSITION")){
            templatePermissionDTO = new TemplatePermissionCompositionDTO();
            templatePermissionDTO.setExportTemplatePermission(entity.getE9_US24_EXPORT_TEMPLATE_DATA());
            templatePermissionDTO.setPreviewTemplatePermission(entity.getE9_US81_PREVIEW_TEMPLATE_STANDARD());
            templatePermissionDTO.setRollbackPermission(entity.getE9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE());
            templatePermissionDTO.setEditMetadataPermission(entity.getE9_US75_EDIT_TEMPLATE_METADATA_STANDARD());
            templatePermissionDTO.setCreateNewVersionPermission(entity.getE9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED());
        }
		if (templatePermissionDTO != null) {
			templatePermissionDTO.setName(entity.getName());
			templatePermissionDTO.setType(entity.getType());
		}
        log.info("Convert {} to templatePermission dto was finished successfully", entity);
        return templatePermissionDTO;
    }


    @Override
    public Page<DataCollectionPermissionDTO> mapDataCollectionPermissions(final Page<DataCollectionPermissionsModel> entities) {
        return entities.map(this::mapDataCollectionPermission);
    }

    @Override
    public Page<ResourcePermissionDTO> mapResourcePermissions(final Page<ResourcePermissionModel> entities) {
        return entities.map(this::mapResourcePermission);
    }

    private ResourcePermissionDTO mapResourcePermission(final ResourcePermissionModel entity) {
        log.info("Convert {} to resourcePermission dto was started", entity);
        ResourcePermissionDTO permissionDTO = null;
        if (entity.getResourceType().equals("IMAGE")) {
            permissionDTO = new ResourcePermissionImageDTO();
            permissionDTO.setDeleteResource(entity.getE8_US66_DELETE_RESOURCE_IMAGE());
            permissionDTO.setEditResourceMetadata(entity.getE8_US55_EDIT_RESOURCE_METADATA_IMAGE());
            permissionDTO.setRollBackResource(entity.getE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE());
            permissionDTO.setCreateNewVersionResource(entity.getE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE());
        } else if (entity.getResourceType().equals("STYLESHEET")) {
            permissionDTO = new ResourcePermissionStylesheetDTO();
            permissionDTO.setCreateNewVersionResource(entity.getE8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET());
            permissionDTO.setEditResourceMetadata(entity.getE8_US61_EDIT_RESOURCE_METADATA_STYLESHEET());
            permissionDTO.setDeleteResource(entity.getE8_US66_2_DELETE_RESOURCE_STYLESHEET());
            permissionDTO.setRollBackResource(entity.getE8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET());
        } else if (entity.getResourceType().equals("FONT")) {
            permissionDTO = new ResourcePermissionFontDTO();
            permissionDTO.setCreateNewVersionResource(entity.getE8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT());
            permissionDTO.setEditResourceMetadata(entity.getE8_US58_EDIT_RESOURCE_METADATA_FONT());
            permissionDTO.setDeleteResource(entity.getE8_US66_1_DELETE_RESOURCE_FONT());
            permissionDTO.setRollBackResource(entity.getE8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT());
        }
        if(permissionDTO != null) {
        	permissionDTO.setName(entity.getName());
        	permissionDTO.setType(entity.getType());
        }
        log.info("Convert {} to resourcePermission dto was finished successfully", entity);
        return permissionDTO;
    }

    private DataCollectionPermissionDTO mapDataCollectionPermission(final DataCollectionPermissionsModel entity) {
        log.info("Convert {} to dataCollectionPermission dto was started", entity);
        final DataCollectionPermissionDTO permissionDTO = new DataCollectionPermissionDTO();
        permissionDTO.setName(entity.getName());
        permissionDTO.setType(entity.getType());
        permissionDTO.setEditDataCollectionMetadata(entity.getE6_US34_EDIT_DATA_COLLECTION_METADATA());
        permissionDTO.setRollBackDataCollection(entity.getE6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION());
        permissionDTO.setCreateNewVersionOfDataCollectionUsingJson(entity.getE6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON());
        permissionDTO.setDeleteDataCollection(entity.getE6_US38_DELETE_DATA_COLLECTION());
        permissionDTO.setCreateNewDataSampleBasedOnJsonFile(entity.getE7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE());
        permissionDTO.setDeleteDataSample(entity.getE7_US50_DELETE_DATA_SAMPLE());
        permissionDTO.setCreateNewVersionOfDataSample(entity.getE7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE());
        permissionDTO.setEditSampleMetadata(entity.getE7_US47_EDIT_SAMPLE_METADATA());
        log.info("Convert {} to dataCollectionPermission dto was finished successfully", entity);
        return permissionDTO;
    }
}
