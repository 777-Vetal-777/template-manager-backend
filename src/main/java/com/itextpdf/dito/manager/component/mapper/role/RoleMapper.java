package com.itextpdf.dito.manager.component.mapper.role;

import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.update.RoleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;

import java.util.Set;
import org.springframework.data.domain.Page;

public interface RoleMapper {
    RoleEntity map(RoleCreateRequestDTO dto);

    RoleEntity map(RoleUpdateRequestDTO dto);

    RoleDTO map(RoleEntity entity);

    RoleDTO mapWithoutUsers(RoleEntity entity);

    Page<RoleDTO> map(Page<RoleEntity> entities);

    Set<RoleDTO> map(Set<RoleEntity> entities);
}
