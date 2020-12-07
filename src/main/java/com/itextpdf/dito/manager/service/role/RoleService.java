package com.itextpdf.dito.manager.service.role;

import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleEntity create(RoleEntity roleEntity, List<String> permissions);

    Page<RoleEntity> list(Pageable pageable, String searchParam);

    RoleEntity update(String roleName, RoleEntity updatedRole, List<String> permissions);

    void delete(String name);
}
