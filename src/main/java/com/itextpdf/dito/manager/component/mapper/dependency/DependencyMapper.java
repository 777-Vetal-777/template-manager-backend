package com.itextpdf.dito.manager.component.mapper.dependency;

import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;

public interface DependencyMapper {
    DependencyDTO map(DataCollectionEntity entity);
}
