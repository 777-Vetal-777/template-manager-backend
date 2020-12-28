package com.itextpdf.dito.manager.service.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.role.RoleFilter;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleEntity get(String name);

    Page<RoleEntity> getByResource(Pageable pageable, ResourceEntity resource);

    RoleEntity create(RoleEntity roleEntity, List<String> permissions);

    Page<RoleEntity> list(Pageable pageable, RoleFilter filter, String searchParam);

    RoleEntity update(String roleName, RoleEntity updatedRole, List<String> permissions);

    void delete(String name);
}
