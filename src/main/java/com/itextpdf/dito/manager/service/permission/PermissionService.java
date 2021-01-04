package com.itextpdf.dito.manager.service.permission;

import com.itextpdf.dito.manager.entity.PermissionEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    PermissionEntity get(String name);

    Page<PermissionEntity> list(Pageable pageable, String searchParam);

    List<PermissionEntity> list();

    List<PermissionEntity> defaultPermissions();
}
