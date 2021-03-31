package com.itextpdf.dito.manager.component.mapper.role;

import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.role.update.RoleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;

import java.util.Set;

import com.itextpdf.dito.manager.model.role.RoleModel;
import com.itextpdf.dito.manager.model.role.RoleWithUsersModel;
import org.springframework.data.domain.Page;

public interface RoleMapper {
    RoleEntity map(RoleUpdateRequestDTO dto);

    RoleDTO map(RoleEntity entity);

    RoleDTO mapModel(RoleWithUsersModel roleModel);

    Page<RoleDTO> mapModels(Page<RoleWithUsersModel> models);

    Page<RoleDTO> mapRoleModels(Page<RoleModel>models);

    RoleDTO mapRoleModel(RoleModel model);

    RoleDTO mapWithoutUsers(RoleEntity entity);

    Page<RoleDTO> map(Page<RoleEntity> entities);

    Set<RoleDTO> map(Set<RoleEntity> entities);
}
