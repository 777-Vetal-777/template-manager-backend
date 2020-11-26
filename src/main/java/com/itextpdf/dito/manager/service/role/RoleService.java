package com.itextpdf.dito.manager.service.role;

import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleDTO create(RoleEntity roleEntity);

    Page<RoleEntity> list(Pageable pageable, String searchParam);

    RoleDTO update(RoleEntity entity);

    void delete(String name);
}
