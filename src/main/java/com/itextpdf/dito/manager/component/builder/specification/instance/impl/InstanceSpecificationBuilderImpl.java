package com.itextpdf.dito.manager.component.builder.specification.instance.impl;

import com.itextpdf.dito.manager.component.builder.specification.instance.InstanceSpecificationBuilder;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class InstanceSpecificationBuilderImpl implements InstanceSpecificationBuilder {

    @Override
    public Specification<InstanceEntity> build(final InstanceFilter instanceFilter, final String searchParam) {
        return null;
    }
}
