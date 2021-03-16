package com.itextpdf.dito.manager.service.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;

import java.util.List;

import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.model.role.RoleModel;
import com.itextpdf.dito.manager.model.role.RoleWithUsersModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleEntity getMasterRole(String name);

    RoleEntity getSlaveRole(String name, ResourceEntity resourceEntity);

    RoleEntity getSlaveRole(String name, DataCollectionEntity dataCollectionEntity);

    RoleEntity getSlaveRole(String name, TemplateEntity templateEntity);

    Page<RoleEntity> getSlaveRolesByResource(Pageable pageable, RoleFilter filter, ResourceEntity resource);

    Page<RoleEntity> getSlaveRolesByDataCollection(Pageable pageable, DataCollectionPermissionFilter filter, DataCollectionEntity dataCollection);

    Page<RoleEntity> getSlaveRolesByTemplate(Pageable pageable, TemplatePermissionFilter filter, TemplateEntity templateEntity);

    RoleEntity create(String name, List<String> permissions, Boolean master);

    Page<RoleWithUsersModel> list(Pageable pageable, RoleFilter filter, String searchParam);

    Page<RoleModel> getRolesByUserSearch(Pageable pageable, String search);

    RoleEntity update(String roleName, RoleEntity updatedRole, List<String> permissions);

    void deleteMasterRole(String name);

    void delete(RoleEntity roleEntity);
}
