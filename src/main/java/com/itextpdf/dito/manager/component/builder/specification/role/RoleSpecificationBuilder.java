package com.itextpdf.dito.manager.component.builder.specification.role;

import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.entity.RoleEntity;

import org.springframework.data.jpa.domain.Specification;

public interface RoleSpecificationBuilder {
    Specification<RoleEntity> build(RoleFilter roleFilter, String searchParam);
}
