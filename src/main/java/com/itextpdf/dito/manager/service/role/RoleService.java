package com.itextpdf.dito.manager.service.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.role.RoleFilter;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleEntity getMasterRole(String name);

    RoleEntity getSlaveRole(String name, ResourceEntity resourceEntity);

    Page<RoleEntity> getSlaveRolesByResource(Pageable pageable, RoleFilter filter, ResourceEntity resource);

    RoleEntity create(String name, List<String> permissions, Boolean master);

    Page<RoleEntity> list(Pageable pageable, RoleFilter filter, String searchParam);

    RoleEntity update(String roleName, RoleEntity updatedRole, List<String> permissions);

    void deleteMasterRole(String name);

    void delete(RoleEntity roleEntity);
}
