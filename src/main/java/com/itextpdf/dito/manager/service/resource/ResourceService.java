package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.FontTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    ResourceEntity create(String name, ResourceTypeEnum type, byte[] data, String fileName, String email);

    ResourceEntity createNewFont(String email, String resourceName, ResourceTypeEnum type, Map<FontTypeEnum, MultipartFile> fonts);

    ResourceEntity createNewVersion(String name, ResourceTypeEnum type, byte[] data, String fileName, String email, String comment);

    ResourceEntity get(String name, ResourceTypeEnum type);

    ResourceEntity getByUuid(String uuid);

    ResourceEntity update(String name, ResourceEntity entity, String mail);

    ResourceEntity delete(String name, ResourceTypeEnum type, String mail);

    @PreFilter(filterTarget = "permissions", value = "@permissionHandlerImpl.checkPermissionsByType(filterObject, #resourceType)")
    ResourceEntity applyRole(String resourceName, ResourceTypeEnum resourceType, String roleName, List<String> permissions);

    ResourceEntity detachRole(String name, ResourceTypeEnum type, String roleName);

    Page<ResourceModelWithRoles> list(Pageable pageable, ResourceFilter filter, String searchParam);

    List<ResourceEntity> list();

    ResourceEntity getResource(String name, ResourceTypeEnum type);

    ResourceFileEntity getFile(String uuid);
}
