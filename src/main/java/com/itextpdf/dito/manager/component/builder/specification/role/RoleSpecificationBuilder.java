package com.itextpdf.dito.manager.component.builder.specification.role;

import com.itextpdf.dito.manager.dto.role.filter.RoleFilterDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;

import org.springframework.data.jpa.domain.Specification;

public interface RoleSpecificationBuilder {
    Specification<RoleEntity> build(RoleFilterDTO roleFilterDTO, String searchParam);
}
