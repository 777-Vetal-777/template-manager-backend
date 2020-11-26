package com.itextpdf.dito.manager.component.mapper.role;

import com.itextpdf.dito.manager.dto.role.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import org.springframework.data.domain.Page;

public interface RoleMapper {
    RoleEntity map(RoleCreateRequestDTO dto);

    RoleDTO map(RoleEntity entity);

    Page<RoleDTO> map(Page<RoleEntity> entities);
}
