package com.itextpdf.dito.manager.component.builder.specification.instance;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;

import org.springframework.data.jpa.domain.Specification;

public interface InstanceSpecificationBuilder {
    Specification<InstanceEntity> build(InstanceFilter instanceFilter, String searchParam);
}
