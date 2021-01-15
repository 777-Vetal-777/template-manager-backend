package com.itextpdf.dito.manager.component.mapper.permission;

import com.itextpdf.dito.manager.dto.permission.DataCollectionPermissionDTO;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.dto.permission.ResourcePermissionDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;

import java.util.Collection;
import java.util.List;

import com.itextpdf.dito.manager.model.resource.ResourcePermissionModel;
import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;

import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import org.springframework.data.domain.Page;

public interface PermissionMapper {
    PermissionDTO map(PermissionEntity entity);

    PermissionEntity map(PermissionDTO dto);

    List<PermissionDTO> map(Collection<PermissionEntity> entities);

    Page<PermissionDTO> map(Page<PermissionEntity> entities);

    Page<DataCollectionPermissionDTO> mapDataCollectionPermissions(Page<DataCollectionPermissionsModel> entities);

    List<TemplatePermissionDTO> mapTemplatePermissions(List<TemplatePermissionsModel> entities);

    Page<TemplatePermissionDTO> mapTemplatePermissions(Page<TemplatePermissionsModel> entities);

    Page<ResourcePermissionDTO> mapResourcePermissions(Page<ResourcePermissionModel> entities);

}
