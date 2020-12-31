package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;

import java.util.List;

import com.itextpdf.dito.manager.filter.role.RoleFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourceService {
    ResourceEntity create(String name, ResourceTypeEnum type, byte[] data, String fileName, String email);

    ResourceEntity createNewVersion(String name, ResourceTypeEnum type, byte[] data, String fileName, String email,
            String comment);

    ResourceEntity get(String name, ResourceTypeEnum type);

    ResourceEntity update(String name, ResourceEntity entity, String mail);
    
    ResourceEntity delete(String name, ResourceTypeEnum type);

    ResourceEntity applyRole(String resourceName, ResourceTypeEnum resourceType, String roleName, List<String> permissions);

    ResourceEntity detachRole(String name, ResourceTypeEnum type, String roleName);

    Page<RoleEntity> getRoles(Pageable pageable, String name, ResourceTypeEnum type, RoleFilter filter);

    Page<ResourceEntity> list(Pageable pageable, ResourceFilter filter, String searchParam);

    ResourceEntity getResource(String name, ResourceTypeEnum type);

}
