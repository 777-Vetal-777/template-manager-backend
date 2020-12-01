package com.itextpdf.dito.manager.component.mapper.permission;

import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;

public interface PermissionMapper {
    PermissionDTO map(PermissionEntity entity);

    PermissionEntity map(PermissionDTO dto);

    List<PermissionDTO> map(Collection<PermissionEntity> entities);

    Page<PermissionDTO> map(Page<PermissionEntity> entities);
}
