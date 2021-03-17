package com.itextpdf.dito.manager.service.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.filter.role.RoleFilter;

import java.util.List;

import com.itextpdf.dito.manager.filter.role.RoleUserFilter;
import com.itextpdf.dito.manager.model.role.RoleModel;
import com.itextpdf.dito.manager.model.role.RoleWithUsersModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleEntity getMasterRole(String name);

    RoleEntity getSlaveRole(String name, ResourceEntity resourceEntity);

    RoleEntity getSlaveRole(String name, DataCollectionEntity dataCollectionEntity);

    RoleEntity getSlaveRole(String name, TemplateEntity templateEntity);

    RoleEntity create(String name, List<String> permissions, Boolean master);

    Page<RoleWithUsersModel> list(Pageable pageable, RoleFilter filter, String searchParam);

    Page<RoleModel> getRolesByUserSearch(Pageable pageable, RoleUserFilter filter,  String search);

    RoleEntity update(String roleName, RoleEntity updatedRole, List<String> permissions);

    void deleteMasterRole(String name);

    void delete(RoleEntity roleEntity);
}
