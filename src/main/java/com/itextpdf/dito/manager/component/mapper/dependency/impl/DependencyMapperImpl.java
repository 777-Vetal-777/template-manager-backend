package com.itextpdf.dito.manager.component.mapper.dependency.impl;

import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;

import org.springframework.stereotype.Component;

@Component
public class DependencyMapperImpl implements DependencyMapper {
    @Override
    public DependencyDTO map(final DataCollectionEntity entity) {
        final DependencyDTO dependency = new DependencyDTO();
        dependency.setName(entity.getName());
        dependency.setDependencyType(DependencyType.DATA_COLLECTION);
        dependency.setActive(Boolean.TRUE);
        // dependency.setVersion(entity.getModifiedOn());
        dependency.setDirectionType(DependencyDirectionType.HARD);
        return dependency;
    }
}
